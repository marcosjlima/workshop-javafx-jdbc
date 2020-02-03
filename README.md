Aplicação de exemplo com JavaFX e MySQL

Para implantar

1 - Baixar JDK, configurar a variável de ambiente JAVA_HOME;

1 - Baixar JavaFX (https://gluonhq.com/products/javafx/);

1.1 - Descompactar em um diretório, ex: C:\JavaLibs\javafx-sdk;

1.2 - Criar a variável de ambiente PATH_TO_FX com o caminho do JavaFX, ex: C:\JavaLibs\javafx-sdk\lib;

2 - Copiar a lib do MySQL para o diretório do item 1.2;

3 - Copiar o arquivo db.properties para o mesmo diretório do .jar da aplicação;

4 - Para executar usar o comando do abaixo:

  java --module-path %PATH_TO_FX% --add-modules javafx.controls,javafx.fxml -cp workshop-javafx-jdbc.jar application.Main
