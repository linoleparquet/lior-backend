## Presentation

This project is the backend part of the [lior project](https://github.com/linoleparquet/lior). It's a Spring Boot application.
It exposes the port `8080`

### Technical aspects

The VPRTW, [Jsprit toolkit](https://jsprit.github.io/)

The OSRM Project

## Build the application

You can directly build the docker image by using:
`docker build -t lior-backend .`

## Release a version

New version are released from the master branch.
First create a tag on the local branch. Only tags starting by 'v' will trigger the release of a docker image. Example: `git tag 'v1.0.1'`
Then push the local tag to the remote branch. Example: `git push origin v1.0.1`
The docker image will be available for pulling. `docker pull ghcr.io/linoleparquet/lior-frontend:v1.0.1`

## Choice of the database to use

This project is designed to be able to run with two different database: A in memory database, `h2`, and a traditionnal database, `PostgreSQL`.
Postgres is the default choice.

- Use the Postgres instance
  Make sure you have a postgres instance running with the postgress password set to password: `docker run --name some-postgres -e POSTGRES_PASSWORD=password -d -p 5432:5432 postgres`
  Launch the application with the default spring profile : `java fr.lino.layani.lior.LiorApplication`

- Run with a H2 instance
- Launch the applicatoin with the `h2` spring profile: `java -Dspring.profiles.active=h2 lino.layani.lior.LiorApplication`
  You can set the Spring profile in IntelliJ throught Edit Configurations... > Modify options > Add VM options
  Or Docker option: `docker run -p8080:8080--rm lior-backend java -jar -Dspring.profiles.active=h2 app.jar`

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
