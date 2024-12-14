# ClientPathFinder

Kod mozna uruchomic poleceniami
```
#!/bin/bash
mvn clean install
mvn exec:java
```
lub
```
./run.sh
```

# Docker
Na czystej maszynie mozna uruchomic obaraz dokera bez instalowania dependecji reczenie. Jezeli chce sie ręcznie to trzeba pobrac na maszyne jave oraz mavena i uruchomic projekt za pomocą mavena. Albo za pomoca intelja
```
docker build -t pathfinderclient:v1.0 .
```

# TODO
- Sprawdzenie dla jakiego oświetelenia najlepiej działą kamerka (badanie)
- Sprawdzenie dla jakiego tresholud binaryzacji najlepiej działą kamerka (badanie)
  
# TODO TESTER:
- Obsługa wyjatków (kod wywala bład jezeli nie ma portu albo kamerki kod sie wywala, moze sprawdzic czy jest podpiety i zwórcic komunikak ze nie podłaczona wtedy kod nie bedzie sie wywalał tylko bedzia nas informował)
- Komenetrze do pliku konfiguracyjengo i wiecej parametrów
- Napisać Testy *lepiej napisac debug*
- Zliczanie i wyświetlanie parametrów z jakimi wykonuje się (kod fps kamerki, czas wysyłania)
- Możliwość sterowania robotem
  

# TODO SOFTWARE:
- Napisanie różnych strategi


# Godziny znarowane na konfiguracje javy
✅20.11.2024 6 godzniy</br>
✅21.11.2024 5 godzniy
### łącznie = 11 godzin

# Przydatne linki
https://github.com/bytedeco/javacpp-presets/tree/master/opencv#documentation
