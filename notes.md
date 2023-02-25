# Kafka Study Notes

## Alguns comandos
Podemos utilizar o Kafka localmente, baixando os arquivos binários do mesmo. Juntamente com eles, precisamos baixar os binários do ZooKeeper.<br>
Com ambos os projetos, podemos, dentro da pasta dos arquivos do Kafka, executar o seguinte comando `bin/zookeeper-server-start.sh config/zookeeper.properties` que vai fazer subir o ZooKeeper. Na sequência, executamos `bin/kafka-server-start.sh config/server.properties` que vai iniciar o Kafka na porta padrão 9092.<br>
Para vermos os possíveis comandos do Kafka, pode os rodar o comando `bin/kafka-topics.sh`.<br>

Podemos criar um novo tópico, executando, por exemplo, o seguinte comando `bin/kafka-topics.sh --create --boostrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic LOJA_NOVO_PEDIDO`.<br>
Listar os tópicos `bin/kafka-topics.sh --list --bootstrap-server localhost:9092`.
<br>

Para enviar uma mensagem para o tópico, podemos fazer da seguinte forma, acessamos os console de producer, indicando onde está rodando o Kafka e qual o tópico: `bin/kafka-console-producer.sh --broker-list localhost:9092 --topic LOJA_NOVO_PEDIDO`, isso vai abrir um console e cada linha será como uma nova mensagem.<br>
Para consumir, acessamos o console de consumer, e indicamos desde quando desejamos receber as mensagens, dessa forma, por exemplo: `bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic LOJA_NOVO_PEDIDO --from-beginning`.
<br>

Ver informações do tópico: `bin/kafka-topics.sh --describe --bootstrap-server localhost:9092`.
<br>


Ver informações dos consumer groups: `bin/kafka-consumer-groups.sh --all-groups  --bootstrap-server localhost:9092 --describe`.
<br>