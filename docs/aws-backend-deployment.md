# Backend Deployment to AWS EC2 with RDS

## Prerequisites
- AWS account (free-tier EC2 t3.micro, Ubuntu 22.04 AMI).
- RDS PostgreSQL running (from previous PR; endpoint: [placeholder]).
- Built JAR: `./mvnw clean package` → target/supply-chain-backend.jar (includes deps: Spring Boot, JPA, Kafka, OAuth).

## Step-by-Step Deployment
1. **Launch EC2**: Console → EC2 → Launch → t3.micro, Ubuntu AMI → Create key pair (supply-chain-key.pem) → Launch. Note public IP (e.g., 3.123.45.67).
2. **Configure Security Group**: Attach group 'supply-chain-sg'—Inbound: SSH 22 (your IP), HTTP 8080 (team IPs/0.0.0.0/32 for testing), HTTPS 443 (future).
3. **Connect & Setup**: SSH: `ssh -i supply-chain-key.pem ubuntu@[ec2-ip]`. Update: `sudo apt update && sudo apt install openjdk-17-jre default-jdk git`. Create dir: `mkdir /app && cd /app`.
4. **Deploy JAR**: SCP from local: `scp -i key.pem target/supply-chain-backend.jar ubuntu@[ec2-ip]:/app/`. On EC2: `cd /app && nohup java -jar -Dspring.profiles.active=aws supply-chain-backend.jar &` (or systemd below).
5. **Systemd Service** (for auto-start): `sudo nano /etc/systemd/system/supply-chain.service`:

[Unit] Description=Supply Chain Backend

[Service] WorkingDirectory=/app

ExecStart=/usr/bin/java -jar /app/supply-chain-backend.jar -Dspring.profiles.active=aws

SuccessExitStatus=143 Environment="DB_PASSWORD=[env]"  # Secure

Restart=always

[Install] WantedBy=multi-user.target

`sudo systemctl daemon-reload && sudo systemctl enable --now supply-chain`. Logs: `journalctl -u supply-chain -f`.
6. **Integrate RDS/Kafka/OAuth**: App properties (env): DB_URL=aws RDS endpoint, GOOGLE_CLIENT_ID=... (set via `export` or systemd override).
7. **Test**: curl [ec2-ip]:8080/api/cargo (or Postman) → Responses OK. Scale: Add CloudWatch alarms; auto-scale group for traffic.

## Monitoring/Scalability
- Correctness: APIs return exact data (e.g., POST Cargo → GET matches DB in RDS).
- Efficiency: <200ms GETs (ping EC2 + query).
- Scalability: t3.micro handles 50+ req/min; upgrade to m5.large or ECS for prod.

Deployed IP: [your-ec2-public-ip]:8080 (team access via VPN/IP restrict).