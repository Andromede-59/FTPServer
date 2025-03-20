# Implémentation de la commande Tree suite à l'obtention de fichiers via FTP
Julien MATTOT<br>
31/01/25

Ce logiciel permet donc d'afficher l'arborescence d'un serveur ftp sous différentes formes. La forme principale est donc via la console avec le même résultat qu'un commande ```tree```. Il est ensuite possible de mettre le résultat du programme dans un fichier json avec une option.

## Instructions de build et d'exécution

<u>Pour empaqueter le projet, suivez les instructions suivantes :</u>

- Positionnez vous dans le dossier ```my-app```
- Exécutez la commande suivante : 
``` mvn package```
- Le projet devrait maintenant être compilé

<u>Pour lancer le projet, exécutez les commandes suivantes :</u>

- Pour lancer le projet de manière classique : 
``` java -jar target/treeFTP-1.0.jar [adresse du ftp]```

- Pour afficher le menu d'aide : 
``` java -jar target/treeFTP-1.0.jar [adresse du ftp] -h ```

- Pour Spécifier la profondeur maximum (un menu s'ouvrira dans la console): 
``` java -jar target/treeFTP-1.0.jar [adresse du ftp] -d ```

- Pour spécifier une sortie dans un fichier json : 
``` java -jar target/treeFTP-1.0.jar [adresse du ftp] -j ```

Les commandes sont bien sur cumulables (-j et -d)

##  Architecture

Le projet a été séparé au maximum pour respecter la méthode SOLID. Il est de plus extensible facilement grâce a son implémentation modulaire.

En effet, chaque partie a été séparée afin de permettre une extension pour chaque module. On peut retrouver plusieurs interfaces comme Connection ou OutputHandler qui permettent d'ajouter d'autres moyens d'afficher le résultat du programme (ici 2 classes descendent d'OutputHanled : ConsoleOutputHanlder et JsonOutputHandler) ou encore d'autres types de connexion (comme ftps par exemple plutôt que ftp). 

## Tests 

Des tests unitaires ont été effectués via la librairie fakeftpserver notamment pour la méthode login ainsi que la méthode generateTree (avec ou sans profondeur). Il était compliqué de réaliser plus de tests unitaires car la plupart des fonctions sont privées car elle n'ont pas besoin d'être appelées en dehors de la fonction. Donc non accessible depuis les tests.

## Tests sur ftp réels

Le programme a été testé et est fonctionnel sur les ftp free, ubuntu et celui du fil. ```ftp.free.fr``` ```ftp.ubuntu.com``` ```webtp.fil.univ-lille.fr``` (Attention, il faut etre connecté au VPN pour accéder au ftp du FIL. Sinon une erreur sera lancée)

En cas de panne d'internet, le programme se met en pause et attend a nouveau la connexion, dès que celle ci revient, le programme reprendra la ou il s'était arrêté.