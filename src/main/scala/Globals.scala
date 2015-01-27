// sample module with global values

import org.scala_tools.time.Imports._

object Globals {
  val applName = "Rentalot"
  val developers = "<your name here>"
  var managerMode = false
  var managerPassword = "1234"

	var movieRentalPeriod = 7.days
  var gameRentalPeriod = 7.days

  // new id generator, test implementation
  // must be replaced by one that does not generate existing ids in a table
  private var c = 60001 //ID numbers for customers
  private var t = 30001 //Title ID numbers for movies and games
  private var d = 1 // DVD ID numbers for individual DVDs
  
  def getNewCustomerId = Sample.customerTable.newRecordID(c)

	def getNewDVDId = {
		var id = Sample.movieTitleTable.newRecordID(d)
		id = Sample.gameTitleTable.newRecordID(id)
		id = Sample.movieTitleTable.newRecordID(id)
		id = Sample.gameTitleTable.newRecordID(id)
		id
	}
  
  def getNewTitleId = {
  	var id = Sample.movieTitleTable.newTitleID(t)
  	id = Sample.gameTitleTable.newTitleID(id)
  	id = Sample.movieTitleTable.newTitleID(id)
  	id = Sample.gameTitleTable.newTitleID(id)
  	id
  	}

  	def saveAll =
  	{
  		Sample.customerTable.save
  		Sample.movieTitleTable.save
  		Sample.movieRentalTable.save
  		Sample.gameTitleTable.save
  		Sample.gameRentalTable.save
  	}
  }