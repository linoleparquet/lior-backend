## Presentation

This project is the backend part of the [lior project](https://github.com/linoleparquet/lior). It's a Spring Boot application.

### Technical aspects
The VPRTW, [Jsprit toolkit](https://jsprit.github.io/)

The OSRM Project

## Build the application 
You can directly build the docker image by using:
`docker build -t lior-backend .`

## TO DO:
- horaires d'ouverture de l'établissmeent par établissment
-demi journée de presence par docteur (faire une énum)
- creer un nouvel objet pour le routing:
 horaires d'ouverture de l'établissement,
jour de disponibilité du médecin
- rajouter next Visit => comment differentier un rdv posé par le docteur d'un rappel?
rajouter un appoitment (bouton bleu) 
  
- hover docteur sur map => afficher information 
- composants graphiques pas uniquement sur toute la longueur de l'écran 
- charte de couleur 
- titre en couleurs
- trouver meilleurs titres (delayed)
- case sensitive filter 