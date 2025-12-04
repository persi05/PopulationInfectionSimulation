Projekt polega na stworzeniu symulacji rozwoju zakażenia wirusem w populacji poruszającej się w dwuwymiarowym obszarze o wymiarach n × m metrów. W obrębie tego obszaru znajdują się osobnicy, którzy przemieszczają się w losowych kierunkach z losową szybkością, nieprzekraczającą 2.5 m/s. Kierunek oraz prędkość mogą ulegać zmianom w trakcie symulacji, jednak z zachowaniem tego ograniczenia.

Kiedy osobnik dotrze do granicy obszaru, może on z prawdopodobieństwem 50% zawrócić do środka lub z takim samym prawdopodobieństwem opuścić obszar. Aby zachować ciągłość populacji, nowi osobnicy wprowadzani są do symulacji w losowych punktach na granicach obszaru, z odpowiednio dobraną częstotliwością oraz początkową liczebnością. Każdy nowy osobnik ma 10% szans na to, że już przy wejściu jest zakażony.

W populacji wyróżnia się osobników odpornych i wrażliwych na zakażenie. Osobnik wrażliwy może znajdować się w jednym z dwóch stanów: zdrowy albo zakażony. Zakażeni dzielą się dodatkowo na posiadających objawy oraz bezobjawowych.

Mechanizm zakażenia opiera się na dwóch warunkach, które muszą być spełnione jednocześnie. Zdrowy osobnik niewykazujący odporności zakazi się od osobnika zakażonego, gdy odległość między nimi nie przekracza 2 metrów oraz gdy odległość ta utrzymuje się przez co najmniej 3 sekundy symulacji. Prawdopodobieństwo zakażenia wynosi 50% w przypadku kontaktu z osobnikiem bezobjawowym i 100% w przypadku kontaktu z osobnikiem objawowym. Czas trwania zakażenia wynosi od 20 do 30 sekund symulacji; po jego upływie osobnik zdrowieje i nabywa odporność.

W symulacji należy uwzględnić dwa warianty populacji: pierwszy, w którym żaden osobnik początkowy ani dołączony nie posiada odporności, oraz drugi, w którym część populacji jest odporna. Ruch osobników modelowany jest za pomocą wektorów, zgodnie z założeniami z wcześniejszych laboratoriów.

System powinien umożliwiać zapis i odczyt stanu symulacji w dowolnym momencie jej trwania, a także wizualizację położenia osobników i procesu rozprzestrzeniania się zakażenia. Każda sekunda symulacji składa się z 25 kroków czasowych, co pozwala na płynne odwzorowanie ruchu i interakcji między osobnikami.


Podsumowanie:
Projekt symuluje rozprzestrzenianie się zakażenia w populacji poruszającej się w dwuwymiarowym obszarze. Zaimplementowano ruch osobników przy użyciu wektorów 2D, model stanu (Healthy, Infected, Immune) w oparciu o wzorzec State oraz zapis i odczyt stanu symulacji ze wzorcem Memento. Symulacja działa w stałym czasie kroków (25 FPS) i umożliwia wizualizację oraz śledzenie statystyk zakażeń - minimalne UI.
