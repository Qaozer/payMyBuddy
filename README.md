# payMyBuddy
 Openclassroom DA JAVA P6


#Installation
``` 
NB: Le projet a été réalisé avec IntelliJ
```

1. Cloner le repo
2. Installer Mysql
3. Exécuter le script contenu dans script.sql
4. Créer un nouvel utilisateur et lui donner les droits sur les databases paymybuddy et paymybuddytest
5. Télécharger [jasypt](https://blog.impulsebyingeniance.io/jasypt-1-9-2-dist/)
6. Extraire le contenu de l'archive
7. Lancer l'invite de commande dans le dossier bin jasypt-1.9.2\bin
8. Utiliser cette ligne de commande pour obtenir le mot de passe crypté :
    ```
    encrypt.bat input=<motdepasse> password=<clésecrete>
   ```
9. Dans application.properties (main et test), effectuer les modificaiton suivantes :
    ```
   spring.datasource.username=<utilisateur>
   spring.datasource.password=ENC(<motdepassecrypté>)```
10. Avant de lancer l'application ou un test, verifier que la variable d'environnement est bien configurée :
    ```
    JASYPT_ENCRYPTOR_PASSWORD=<clésecrete>

#Modèle Physique de Données
<p><img src="img/MPD.png"></p>
