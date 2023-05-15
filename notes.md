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
<br>

É interessante alterar o "config/server.properties" e o "config/zookeeper.properties", indicando um diretório mais adequado para guardar as configurações, pois, por padrão
são usadas pastas dentro da tmp, que, após a reinicialização da máquina serão removidas, pendendo informações, inclusive os tópicos.
Para isso, basta indicar um diretório onde as informações deverão persistir.
<br>

### Replicar broker manualmente
Para replicar o broker de forma manual e garantir que tenhamos mais disponibilidade da aplicação caso um dos brokers caia,
podemos configurar da seguinte forma, copiando as confgurações do broker atual em um novo `cp config/server.properties config/server2.properties`.<br>
Após, modificamos o arquivo e alteramos o valor de "broker.id" para diferenciar do já existente.<br>
Além disse, adequados o diretório dos logs desse broker, modificando a propriedade "log.dirs".<br>
Também precisamos alterar a porta onde rodará esse broker, assim, basta descomentar e trecho que declara a porta e modificar o final da mesma
visto que, por padrão, roda na porta 9092, "listeners=PLAINTEXT://:9093".<br>
Depois disso, inicializamos o novo broker: `bin/kafka-server-start.sh config/server2.properties`.
<br>
Replicar os servidores dos brokers não é o suficiente para ser acessível às aplicações, precisamos alterar o fator de
replicação dos tópicos. Para isso, ajustamos nos arquivos "config/server.properties" e "config.server2.properties" (novo que criamos),
adicionando a propriedade "default.replication.factor=3".<br>
Após, esse ajuste, precisamos parar todos os servidores do Kafka e também do Zookeeper, e apagar os diretórios "data/kafka/*" e
"data/zookeeper/*".
<br>
Ainda para evitar o single point of failure, precisamos alterar o replication factor dos offsets, assim, dentro dos arquivos de propriedades
de configuração, podemos moficar as seguintes propriedades, aumentando os valores delas "offsets.topic.replication.factor=3", 
"transaction.state.log.replication.factor=3", além da "default.replication.factor=3" que já havíamos modificado anteriormente.<br>


## Algumas infos

### Ordering
Vale lembrar que, caso tenhamos várias mensagens cuja key repita, dentro de um mesmo tópico, estas mensagens serão processadas
na ordem em que chegaram ao tópico.<br>
Ou seja, caso um mesmo usuário realize três compras, estas serão processadas sequencialmente, conforme foram feitas, isso em
um cenário em que a key utilizada seja, por exemplo, o e-mail do usuário.
Caso usemos sempre valores aleatórios e que não se repitam como key, poderíamos ter o processamento dessas mensagens
de forma aleatória. É necessário verificar o que é o melhor para o serviço.
<br>

### Acks e reliability
Mesmo replicando as mensagens em vários brokers, pode acontecer de, antes de conseguir replicar as mensagens para
outros brokers (que estariam se recuperando, por exemplo), o leader pode cair, deixando, assim, todos os outros brokers
desatualizados sem saberem.<br>
Podemos tentar sanar esse problema setando uma quantidade de "ACKS", sendo que, só serão consideradas enviadas as mensagens
quando uma quantidade "x" de brokers responderem que receberam.<br>
Essa quantidade pode ser configurada e, segundo a documentação, estes são so valores:
"The number of acknowledgments the producer requires the leader to have received before considering a request complete. 
This controls the  durability of records that are sent. The following settings are allowed:  <ul> <li><code>acks=0</code> If set to zero then the producer will not wait for any acknowledgment from the server at all. The record will be immediately added to the socket buffer and considered sent. No guarantee can be made that the server has received the record in this case, and the <code>retries</code> configuration will not take effect (as the client won't generally know of any failures). The offset given back for each record will always be set to -1. <li><code>acks=1</code> This will mean the leader will write the record to its local log but will respond without awaiting full acknowledgement from all followers. In this case should the leader fail immediately after acknowledging the record but before the followers have replicated it then the record will be lost. <li><code>acks=all</code> This means the leader will wait for the full set of in-sync replicas to acknowledge the record. This guarantees that the record will not be lost as long as at least one in-sync replica remains alive. This is the strongest available guarantee. This is equivalent to the acks=-1 setting."
Assim, é interessante usar, por exemplo, o valor "all" que garante que todos os disponíveis receeram a mensagem para considerá-la
de fato enviada.
<br>

### Max poll
Podemos definir a quantidade de mensagens que serão lidas/processadas por vez. No caso de deixarmos apenas 1 por vez
temos uma maior segurança de que, caso algum dos brokers caia, mensagens não se percam no rebalanceamento.
Por exemplo, caso tenhamos 2 brokers, sendo um com 3 partições e outro com 1, caso esse de 2 partições caia e tenha
consumido 5 mensagens por vez, ao rebalancear para o único broker restante, pode ser que haja uma perda de informação
na hora de resetar os offsets.<br>
Consumindo uma por vez e commitando uma por vez, há uma segurança maior de que não foram perdidas mensagens caso haja
a necessidade de rebalancear.
<br>

### In flight requests per connection
Propriedade `retries` se tiver valor maior que zero vai permitir ao client reenviar qualquer mensagem que falhou no envio.
Permitir retentativas (propriedade acima) sem setar a propriedade `max.in.flight.requests.per.connection` com o valor "1"
pode ocasionar uma mudança na ordem dos registros, pois se dois batches estão enviando para a mesma partição e um deles
falha e é retentado, e o outro tem sucesso no envio, então as mensagens enviadas pelo segundo vão aparecer primeiro na partição.<br>
O Kafka não consegue gatantir a ordem em que as mensagens vão chegar nas partições, pois por causa das retentativas
pode ser que uma acabe chegando antes da outra; consegue apenas garantir a ordem de consumo, que é a mesma que a ordem 
em que chegaram na partição.
<br>
A propriedade `max.in.flight.requests.per.connection` é o número de requisições "unacknowledged" que o cliente envia em
uma única conexão antes de bloquear, e o valor padrão é 5.<br>
Portanto, setar esse valor com > 1 e falhar no envio gera o risco de as mensagens serem reordenadas por causa das retentativas
de envio.
<br>

### Offset padrão no reset
Caso o offset guardado como "próximo" tenha se perdido, é importante configurar a propriedade `auto.offset.reset`
do Kafka a qual indica se é para voltar a consumir do começo, das mensagens mais antigas ainda disponíveis ("earliest"),
ou da última mensagem "pra frente", ou seja, apenas as novas que chegarem ("latest").
<br>
Essa configuração pode ser feita por consumer group.
<br>

### Algumas configs (consumer) para impedir mensagens duplicadas
Uma configuração que pode ser feita é setar `enable.auto.commit.config` para `false`, para que o consumer controle
manualmente o offset. Também para evitar o comportamento de commitar a mensagem enquanto ela ainda está sendo processada.<br>
O `isolation.level.config` também pode ser setado para `read_commited`, isso vai fazer com que o consumer apenas
leia as mensagens que estejam adequadamente commitadas em todos os brokers, por exemplo, caso o producer envie
para 1 broker e o consumer já saia consumindo, caso o acks seja all pro producer e ele não consiga enviar para
todos os brokers, pode ser que ele reenvie a mensagem novamente para todos, e isso pode gerar duplicidade de mensagem.
<br>

### Idempotência
