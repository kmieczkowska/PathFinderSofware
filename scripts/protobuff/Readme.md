## Wprowadzenie do Google Protocol Buffer

Protocol Buffer - wieloplatformowy i otwartoźródłowy format wymiany danych komputerowych wykorzystywany do serializacji danych strukturalnych.
Został przygotowany przez Google w celu przechowywania i przesyłania dowolnych ustrukturyzowanych informacji w postaci binarnej.
U nas używamy do przesłania dancyh pomiedzy komputerami.

### Artykuł na temat protobuff:
https://www.baeldung.com/google-protocol-buffer

## Jak pracowac z protobuff?

1. Pierwszym krokiem jest stworzenie pilku z rozszerzeniem *.proto który ma struktóre podobną do xmla.
 przykład składni protobuf:
```
syntax = "proto3";

option java_outer_classname = "ClientPackageClass";

message ClientPackage {
  string strategy = 1;
  int32 motorASpeed = 2;
  int32 motorBSpeed = 3;
  int32 treshold = 4;
  int32 binarisation = 5;
  int32 cameraStream = 6;
  int32 flashlightBrightness = 7;
}
```
2. Pobrać odpowieni sktrypt jezyka do generowania klass z gihuba: https://github.com/protocolbuffers/protobuf/releases

3. Za pomocą pobranego kodu z gita oraz stworzonego pliku *.proto mozemy wygenerować klasse javy do komunikacji pomedzy kompyterami:

```protoc -I=. --java_out=. package.proto```

po uruchomieniu kodu powinniyśmy wygenerować plik o nazwie: __ClientPackageClas.java__ 

4. Powstały plik dodajemy do naszego kody javy i mozemy go użyć do przesyłania danych binarnie pomeidzy kompyterami:
przykład użycia wygenerowej klasy:
```
# na serwerze:
ClientPackageClass.ClientPackage clientPackage = ClientPackageClass.ClientPackage.newBuilder()
                .setStrategy("Server")
                .setMotorASpeed(100)
                .setMotorBSpeed(100)
                .setTreshold((int) TresholdSlider.getValue())
                .setBinarisation(1)
                .setCameraStream(1)
                .setFlashlightBrightness((int) TresholdSliderLED.getValue())
                .build();
        client.sendCommand(clientPackage);
        
# u clienta:
ClientPackageClass.ClientPackage clientPackage = receiveClientPackage();
imageProcesor.setTreshold(clientPackage.getTreshold());

```