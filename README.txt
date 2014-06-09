========================================
EP3 - MAC0438 Programação Concorrente

Daniel Augusto Cortez

12/06/2014
========================================


CÓDIGO-FONTE
------------

O código-fonte (classes Java) está disponível na pasta src/dacortez/diningSavages/. A implementação 
das funções especificadas para manipulação do monitor está disponível na classe PotMonitor.java, a 
partir da linha 200. Essas funções são apenas wrappers para os métodos que implementam as 
funcionalidades reais na classe ConditionVariable.java.


UTILIZAÇÃO
----------

O código já está compilado no arquivo jar, não há makefile. A versão do Java utilizada foi a 1.7.45.
Para executar o programa, basta digitar

	$> java -jar DiningSavages.jar <arquivo> <R> <U|P>

Onde:
  arquivo    Arquivo de entrada
        R    Número de repetições
        U    Situação uniforme
        P    Situação com peso

Os pontos dos gráficos 1 e 2 são gerados nos arquivos 'grafico_1.txt' e 'grafico_2.txt'.
Para detalhes sobre a implementação, consulte o arquivo 'relatório.pdf'. 


ENTRADAS
--------

Foram incluídos dois arquivos de entrada: 'simples.txt' e 'complexo.txt'. Eles representam os dois 
cenários propostos para estudo e analisados no relatório.
