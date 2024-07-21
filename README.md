# Desafio Técnico

## Geral

Para a implementação do [autorizador](https://caju.notion.site/Desafio-T-cnico-para-fazer-em-casa-218d49808fe14a4189c3ca664857de72), 
escolhi utilizar Java Spring como linguagem e framework junto com o banco de dados H2 em memória, apenas como facilitador 
dos testes unitários e do teste em geral.

Escolhi utilizar uma padrão MVC para o aplicativo web e busquei estruturar o problema com diferentes entidades,
que em resumo representam a transação atráves da classe `Transaction` e os usuários `Merchant` e `User`. Apesar de ambos 
lojista e usuário comum terem diferenças, utilizei nos dois uma mesma classe pai `Account` por entender que ambos 
pudessem ter campos em comum, apesar de não ser necessário para o desafio em si. Já a entidade `Transaction`, 
mesmo não sendo requisito do desafio, utilizei para manter o histórico de transações. Também mapeei o payload para 
autorização de transação na classe `TransactionDTO` para facilitar a validação do JSON recebido a cada requisição para 
o endpoint.

## Problemas L1, L2 E L3

O autorizador simples, com fallback e dependente de comerciante foram implementados juntos na classe de serviço 
`TransactionService`. Para a implementação do autorizador em geral, a função `TransactionService.authorize()`, 
que utiliza funções auxiliares das classes de serviço `MerchantService` e `UserService` junto de outras funções internas 
para processar e retornar o código da transação, dependendo da autorização ou não.

Busquei dividir o processo de autorização em:
- Recebe o payload `TransactionDTO`
- Busca o usuário e o lojista
- Valida o MCC da transação de acordo com os dados do lojista (L3)
- Valida o saldo da carteira do usuário apenas da categoria da transação (L1)
- Caso seja insuficiente, busca validar junto do saldo em **CASH**
- Caso tenha saldo suficiente, autoriza a transação, debitando o saldo da carteira do usuário e atualizando o balanço do 
lojista, retornando o código `"00"` em ambos casos, L1 e L2
- Caso não tenha saldo suficiente, retorna código `"51"`
- Caso ocorra algum exception durante o fluxo (caso não exista o usuário ou o lojista, por exemplo), retorna o 
código `"07"`

Para o problema L3, utilizei um `HashMap` de algumas poucas substrings que já tem um MCC "conhecido". O autorizador valida
o saldo a ser consultado primeiramente buscando no hashmap se o nome da loja contém alguma das substrings conhecidas para 
atribuir um MCC correto. Como melhoria, poderia refinar a validação dos nomes dos lojistas com expressões regulares, 
aumentar o mapeamento de substrings conhecidas, mas acredito que soluções mais complexas possam adicionar um overhead 
desnecessário no processamento da transação. Para o problema em si de identificar o código MCC pelo nome do comerciante, 
não acredito que exista uma solução ótima.

## L4. Questão aberta

Para o problema de transações simultâneas, acredito se tratar de um problema produtor consumidor e eu pensaria
em implementar primeiro duas "estruturas": Fila concorrente para atomicidade das operações e cache para otimizar o tempo 
de processamento. 

- Num cenário de múltiplas aplicações rodando e recorrendo a uma base que contenha os dados do usuário, por exemplo, 
uma fila concorrente poderia transformar o bloco processamento da transação em casa instância em uma operação atômica e 
evitaria o acesso concorrente ao banco de dados. Além disso, a fila também garante a ordem de entrada das operações e em
um cenário em que é pouco comum mais de uma transação em simultâneo, não acredito que o tempo em "lock" na fila seria um problema.
- Já a cache poderia ser implementada como um banco de dados não-relacional chave-valor. O banco poderia servir para otimização 
na busca pelo saldo do cliente, validando a transação de forma mais rápida para balancear com o tempo no aumento de processamento
devido a fila concorrente.

Ambas estruturas podem ser utilizadas em conjunto, com a fila concorrente responsável pela integridade das transações e o
banco não-relacional de chave-valor para diminuir o tempo de processamento das transações.