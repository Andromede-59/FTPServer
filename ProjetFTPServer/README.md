# Implémentation d'un serveur FTP

- MATTOT Julien, julien.mattot.etu@univ-lille.fr

Ce logiciel permet de simuler un serveur ftp directement sur sa machine et pouvoir donc intéragir de la même manière qu'un serveur ftp distant.

## Instruction de build et d'exécution

<font color="red">Vous devez avoir une version java supérieure ou égale à 11 pour pouvoir compiler et exécuter le projet.</font>

- Positionnez vous dans le dossier ```my-app```
- Exécutez la commande suivante : 
``` mvn package```
- Le projet devrait maintenant être compilé

<u>Pour lancer le projet, exécutez les commandes suivantes :</u>

- Pour lancer le projet de manière classique : 
``` sh
java -jar target/ftpserver-1.0-SNAPSHOT.jar [PORT]
```

- Pour afficher le menu d'aide : 
``` sh
java -jar target/ftpserver-1.0-SNAPSHOT.jar -h 
```

## Architecture 

Le projet a été concu pour répartir au maximum les taches demandées par le serveur. 

Dans un premier temps, on retrouve une classe FTPServer qui gère la connexion de tous les clients (pas de limite fixée). Chaque Client est représenté par un Thread afin que la charge serveur soit mieux managée. Chaque Thread (ClientHandler) possède ensuite une session pour le client qui gère à la fois la socket de connexion et la socket de données (pour les transferts). Ce Thread possède aussi une méthode qui récupère la demande du client et exécute la commande demandée. *

Ces commandes sont toutes créées dans une factory, ce qui permet de centraliser toutes les créations de commandes et ainsi, si une commande n'est ps reconnue, de directement récupérer une "UNKNOWN COMMAND" qui dira au client que la commande n'a pas encore été implémentée.

Toutes ces commandes ont donc elles été implémentées suivant de pattern Commandes. Elles héritent toutes de la classe `FtpCommand` dans laquelle on retrouve l'implémentation du `printInConsole` qui permet d'avoir un print spécial dans toutes les consoles.

## Commandes Disponibles 

Voici la liste des commandes disponibles : 

| Commande | Description |
| --- | --- |
| AUTH | Permet de s'authentifier sur le serveur |
| USER | Permet de se connecter au serveur |
| PASS | Permet de se connecter au serveur |
| QUIT | Permet de se déconnecter du serveur |
| PWD | Permet de connaitre le répertoire courant |
| CWD | Permet de changer de répertoire |
| CDUP | Permet de remonter d'un répertoire |
| LIST | Permet de lister les fichiers du répertoire courant |
| RETR | Permet de récupérer un fichier |
| STOR | Permet de stocker un fichier |
| DELE | Permet de supprimer un fichier |
| MKD | Permet de créer un répertoire |
| RMD | Permet de supprimer un répertoire |
| PORT | Permet de changer le mode de transfert de données en mode Actif |
| PASV | Permet de changer le mode de transfert de données en mode Passif |
| SYST | Permet de connaitre le système du serveur |
| TYPE | Permet de changer le type de transfert de données |
| RNFR | Permet de renommer un fichier |
| RNTO | Permet de renommer un fichier |
| FEAT | Permet de connaitre les fonctionnalités du serveur (*) |
| OPTS | Permet de changer les options du serveur (*) |
| WELCOME | Permet de connaitre le message de bienvenue du serveur |
| SIZE | Permet de connaitre la taille d'un fichier |

(*) : Commandes implémentées mais mal supportées via FileZilla donc retirées de la liste des commandes disponibles


##  Tests

Un fichier de couverture de tests est généré via Jacoco lors de la compilation. Il est disponible à l'emplacement `target/site/index.html`. 

Une couverture de plus de 80% a été réalisée. Les tests ont été réalisés en essayant le plus possible d'éviter les répétitions grâce notamment à des tests parametrés. Pour tous les tests qui demandaient un transfert ou de toucher au datasocket, une classe spécifique individuelle leur a été créée. 

Pour les tests de transfert, la bibliothèque fakeftpserver ne fonctionnait pas parfaitement. Nous avons donc décidé de contourner le problème en faisant tourner directement le test sur la machine via des fichiers temporaires qui sont immédiatement supprimés à la fin de l'exécution des tests.

Certains tests ont été implémentés mais commentés car ils ne fonctionnaient pas sur toutes les machines. Ils sont donc disponibles dans le code mais non utilisés.

## Tests sur ftp réels

L'Application a été testée sur `FileZilla` ainsi que via `lftp` avec la commande suivante :

``` sh
 lftp -u anonymous,anonymous ftp://localhost:2121
```

Toutes les commandes ont été testées et fonctionnent correctement dans les scénarios utilisés.