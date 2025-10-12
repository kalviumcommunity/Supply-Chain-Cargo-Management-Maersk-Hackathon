# Kafka Deployment to AWS MSK with Backend Integration

## Prerequisites
- AWS MSK cluster provisioned (from previous EC2/RDS).
- EC2 backend deployed (JAR running with systemd).
- IAM: EC2 role with `AmazonMSKFullAccess` policy for access.

## Step-by-Step MSK Setup
1. **Create MSK Cluster**: Console → MSK → Create cluster → Quick create: 1 broker (kafka.m5.large), v3.5.1, public access (VPC public subnets), monitoring (CloudWatch enabled). Name: supply-chain-msk. ~15 min to available.
2. **Security**: Cluster SG: Inbound 9098 from EC2 SG (ports 9092/9094 for plaintext). IAM auth on (for EC2 role).
3. **Get Bootstrap Servers**: CLI: `aws kafka describe-cluster --cluster-arn [arn] --query 'ClusterInfo.BootstrapBrokerString'` → b-1.abc123:9098,b-2...:9098 (save for config).
4. **Create 5 Topics** (matches project events; 3 partitions, replication 2 for scale):
   CLI: `aws kafka create-topic --topic-name cargo-events --cluster-arn [arn] --number-of-partitions 3 --replication-factor 2`.
   Repeat for: shipment-events, delivery-events, route-events, vendor-events.
   Verify: `aws kafka list-topics --cluster-arn [arn]` → All listed.
5. **Integrate with EC2 Backend**: SSH EC2 → Restart service: Edit systemd for env `KAFKA_BOOTSTRAP=[bootstrap-servers]`. App quickstart.properties: `spring.kafka.bootstrap-servers=${KAFKA_BOOTSTRAP}` (env var). Restart: `systemctl restart supply-chain` → Logs: "Connected to Kafka at [bootstrap]".
6. **Test**: POST /api/cargo → Backend publishes to cargo-events (logs: "Sent to topic"). MSK metrics: Incoming bytes >0. Consumer on EC2: Logs "Consumed..."; WebSocket test receives.

## Monitoring/Scalability
- CloudWatch: MSK metrics (Throughput, ConsumerLag <10ms).
- Prod: Add MSK Connect for streams; auto-scale brokers.

Bootstrap Servers: [placeholder; share privately via IAM]. Cluster ARN: [placeholder].

- EC2 Config: On server, set env (e.g., echo 'KAFKA_BOOTSTRAP=...' >> /etc/environment), restart app. (No repo change—docs only.)

- Test: Postman to EC2 IP /api/cargo POST → Check EC2 logs (publish), MSK console (topic data), consumer logs/WebSocket (consume). Multi-message: 10 POSTs → MSK handles (no lag).