# Description du jeu ICRogue
## Introduction
Alors que vous commencez à réviser pour la période d'examens, vous entendez une voix vous appeler du fond de votre cave.
Vous vous dirigez alors dans votre cave, mais vous glissez et tombez dans un trou et perdez connaissance ...
Quand vous vous réveillez, vous vous trouvez dans une pièce vide et décidez alors d'explorer ces sous-sols dont vous
ignoriez l'existence avant ce moment... 

## Comment jouer ?

### Commandes de base
- 'W-A-S-D' pour se déplacer
- 'Q-E' pour changer d'objet sélectionné dans l'inventaire (resp. gauche-droite)
- 'F' pour interagir avec un objet ou un personnage
- 'X' pour utiliser l'objet sélectionné
- Flèches directionelles pour utiliser l'objet sélectionné dans une direction spécifique (peu importe si l'on se déplace dans une autre direction en même temps)
- 'R' pour réinitialiser le niveau et en créer un nouveau
- 'K' pour parler tout seul (pour rigoler un peu)
### Commandes de debug
- 'O' pour ouvrir tous les connecteurs d'une salle
- 'T' pour ouvrir tous les connecteurs fermés et fermer tous les connecteurs ouverts d'une salle
- 'L' pour lock le connecteur de gauche d'une salle

### Ennemis
- Tourelle : Ne bouge pas, tire des flèches à intervalles réguliers et a 1 point de vie. A une chance de faire apparaître une bombe à sa mort.
    - Flèche : Met un dégât, peut être renvoyée grâce à l'épée
- Squelette : Fonce sur le joueur, a 1 point de vie et met 1 dégât. Meurt lors d'un choc
- Dark Lord : Boss du niveau 0. Se déplace de manière aléatoire au bord de la salle et a 5 points de vie. Tire deux boules de feu squelette de ses deux côtés chaque certain temps.
Il ne prend de dégâts que par les boules de feu qu'il tire lui-même et les bombes.
    - Boule de feu squelette : Met 1 dégât, peut être renvoyée grâce à l'épée. A une certaine chance de faire apparaître un squelette à l'impact avec un mur.
### Objets
- Bombe : Explose et met 1 dégât à toutes les entités présentes dans le rayon de l'explosion. Permet également de casser le connecteur fissuré pour accéder à la salle du forgeron.
- Staff : Permet de lancer des boules de feu dans une direction.
- Épée : Met 1 dégât à l'entité présente sur la case de devant. Permet de renvoyer les flèches des tourelles et les boules de feu du Dark Lord.
- Cerise : Permet de se soigner de 1 point de vie.
- Clé : Permet d'ouvrir les portes. Ne se stocke pas dans l'inventaire, mais est utilisée directement lorsqu'on tente d'ouvrir une porte fermée à clé.
- Diplôme : S'obtient à la fin du jeu. Permet de flex.
### Salles
Le jeu est composé plusieurs types différents de salles principales :
- Les "Item room" :
    Ces salles sont très simples, elles se définissent par la présence d'un objet qui, une fois récolté 
    engendrera la victoire de la salle
- La "Skeleton room" :
    Cette salle est composée d'un squelette. Il faut le tuer pour gagner la salle
- Les "Enemy room" :
    Elles sont peuplées d'ennemis qui doivent mourir pour que la salle soit terminée
- La "Boss room" :
    Dans cette salle se trouve le boss et pour la finir il va falloir réussir à le battre. Une fois le boss 
    vaincu, le niveau est terminé. Pour rentrer dans cette salle il faut avoir la clé donnée par le forgeron.
- La "forgeron room" :
    Dans cette salle se trouve le forgeron, un personnage qui vous sera essentiel pour pouvoir finir.
### Personnages
- Forgeron : Il est situé dans une salle cachée. Donne des indices sur comment battre le boss et donne la clé.
- Roi/Reine : Apparaît à la mort du boss. Offre son diplôme du 1er semestre du bachelor au joueur.
