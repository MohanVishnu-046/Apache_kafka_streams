kafka-topics.bat --create --bootstrap-server localhost:9092 --replication-factor 3 --partitions 3 --topic CustomerRewardsTempTopic --config min.insync.replicas=2