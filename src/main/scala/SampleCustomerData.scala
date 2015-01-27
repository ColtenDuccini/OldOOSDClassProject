
// This module constructs a couple of sample database tables
// which are used in the CustomerUI module to show a possible
// way to display customer data.
// In your implemetation, tables such these should be constructed
// from files instead at system start up time.

object Sample {
  val customerTable = new CustomerPTable("customers.txt")
 // customerTable.clear
 // List((62324,  Customer("Mary", "Campione", "123 Johnson Street, Iowa City, IA", "319-333-4444")),
 //      (62856,  Customer("Alison", "Huml", "4221 Lake Drive, Springfield, IL", "354-775-2224")),
 //      (66782,  Customer("Kathy", "Walrath", "964 Flamingo Road, Los Angeles, CA", "324-234-2353")),
  //     (67373, Customer("Sharon", "Zakhour", "9421 1st Street, Coralville, IA", "319-983-2373")),
  //     (62322, Customer("Abby", "Lane", "964 Flamingo Road, Los Angeles, CA", "324-234-3451")),
  //     (67321, Customer("John", "Brown", "9421 12st Street, Coralville, IA", "319-99-0112")),
  //     (67343, Customer("Philip", "Milne", "823 Melrose Ave, Iowa City, IA", "319-873-2823"))
  //    ) foreach (p => customerTable.update(p._1, p._2))

	val movieTitleTable = new MovieTitlePTable("movie-titles.txt")
//	movieTitleTable.clear
//	List((1, MovieTitle(34434, "The Dark Knight","Christopher Nolan", 2008, "PG-13", 0)), 
 //      (2, MovieTitle(34434, "The Dark Knight","Christopher Nolan", 2008, "PG-13", 1)), 
  //     (3, MovieTitle(33322, "Inception","Christopher Nolan", 2010, "PG-13", 0)), 
   //    (4, MovieTitle(33322, "Inception","Christopher Nolan", 2010, "PG-13", 1)), 
   //    (5, MovieTitle(32382, "Star Wars","George Lucas", 1977, "PG", 0)),
    //   (6, MovieTitle(32382, "Star Wars","George Lucas", 1977, "PG", 1)), 
    //   (7, MovieTitle(37327, "Shutter Island","Martin Scorsese", 2010, "R", 0)),
    //   (8, MovieTitle(37327, "Shutter Island","Martin Scorsese", 2010, "R", 1))
    //   ) foreach (p => movieTitleTable.update(p._1, p._2))
       
  val movieRentalTable = new MovieRentalPTable("movie-rentals.txt")
 // movieRentalTable.clear
//  List((2, MovieRental(34434, 67373, "The Dark Knight", 1, "10/21")), // rented to Sharon
   //    (4, MovieRental(33322, 67373, "Inception", 1, "10/21")), // rented to Sharon
   //    (6, MovieRental(37382, 67373, "Star Wars", 1, "10/21")), // rented to Sharon
   //    (8, MovieRental(32327, 67321, "Shutter Island", 1, "10/21")) // rented to John
  //   ) foreach (p => movieRentalTable.update(p._1, p._2))

  val gameTitleTable = new GameTitlePTable("game-titles.txt")
//  gameTitleTable.clear
//  List((9, GameTitle(35476, "Super Mario Bros.", "Nintendo", 2000, "E", 0)),
 // 		 (10, GameTitle(35476, "Super Mario Bros.", "Nintendo", 2000, "E", 1)),
  //		 (11, GameTitle(35476, "Super Mario Bros.", "Nintendo", 2000, "E", 2)),
  //		 (12, GameTitle(35343, "Halo", "Microsoft", 2001, "M", 0)),
  //		 (13, GameTitle(35343, "Halo", "Mircosoft", 2001, "M", 1)),
  //		 (14, GameTitle(35343, "Halo", "Microsoft", 2001, "M", 2)),
  //		 (15, GameTitle(35396, "Call of Duty", "Infinity Ward", 2001, "M", 0))
  //	 ) foreach (p => gameTitleTable.update(p._1, p._2))
  		 
	val gameRentalTable = new GameRentalPTable("game-rentals.txt")
 // gameRentalTable.clear
//  List((10, GameRental(35476, 67373, "Super Mario Bros.", 1, "10/21")),
  //		 (11, GameRental(35476, 66782, "Super Mario Bros.", 2, "10/21")),
  //		 (13, GameRental(35343, 67321, "Halo", 1, "10/21")),
  //		 (14, GameRental(35343, 67343, "Halo", 2, "10/21"))
  //	 ) foreach (p => gameRentalTable.update(p._1, p._2))
}