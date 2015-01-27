
import scala.swing._
import event._
import Swing._
import javax.swing.table._

import org.scala_tools.time.Imports._

import net.miginfocom.swing._
import in.raam.calc._

import BorderPanel.Position._
import TabbedPane._
import GridBagPanel._

// Sample class for Customer Pane
class CustomersUI(customerTable: CustomerPTable, movieRentalTable: MovieRentalPTable, gameRentalTable: GameRentalPTable) {

  // column names for the UI table
  val columnNames = Array[AnyRef]("ID", "First Name", "Last Name", "Address", "Phone", "Email")

  //---------------------------
  // left pane
  //---------------------------
  val customerListBox = new BoxPanel(Orientation.Vertical) {

    //---------------------------
    // top panel
    //---------------------------
    val searchField = new TextField { 
      columns = 10
    }
    val searchPanel = new FlowPanel(new Label("Search by"), new ComboBox(columnNames), searchField) {
      maximumSize = new Dimension(400,12)
    }
 
    //---------------------------
    // middle panel
    //---------------------------
    val tableModel = new DefaultTableModel(Array[Array[AnyRef]](), columnNames)
    val table = new Table(customerTable.size, columnNames.size) {
      model = tableModel
      preferredViewportSize = new Dimension(500, 50)
      selection.intervalMode = Table.IntervalMode.Single
      selection.elementMode = Table.ElementMode.Row
    }

    for ((id, Customer(fn, ln, ad, ph, em)) <- customerTable.toList) {
      val row = Array[AnyRef](id: java.lang.Integer, fn, ln, ad, ph, em)
      tableModel.addRow(row)
    }

    table.peer.setColumnSelectionAllowed(false)
	   table.peer.setRowSelectionAllowed(true)

    // Select row 0
    table.peer.setRowSelectionInterval(0, 0)

    //---------------------------
    // bottom panel
    //---------------------------
    val addCustomerAction = Action("New Customer") {
    	 // the call to Globals.getNewId should be replaced by one
    	 // to a better ID generation method that does not generate
    	 // the same ID as one of an existing customer
      val newCustomerID = Globals.getNewCustomerId 
    	 
    	 val newRow = Array[AnyRef](newCustomerID:java.lang.Integer, "", "", "", "", "")
    	 customerTable.update(newCustomerID, new Customer("","","","",""))
       table.selection.cells.toList match {
        // if a row is currently selected, insert new row at that position
        case List((i, _)) => tableModel.insertRow(i, newRow)
	       // else, insert at the top
	      case _ => tableModel.insertRow(0, newRow)
      }
    }
    
    val deleteCustomerAction = Action("Delete Customer") {
      if (Globals.managerMode == false) {println("Must be in manager mode to delete customers")}
      else {
      table.selection.cells.toList match {
        // delete only singly selected rows
        case List((i, _)) => {
        	val id = table(i,0).asInstanceOf[Int]
        	if (movieRentalTable.select(p => p.customerID == id).isEmpty == false || gameRentalTable.select(p => p.customerID == id).isEmpty == false){println("Cannot delete a customer with items checked out.")}
        	else { 
          tableModel.removeRow(i)
          println("Deleting customer " + id)
          customerTable.remove(id)
        	}
        }
        case _ => () 
      }
      }
    }

    //
    def rentalFrame = new Frame {
    title = "Check out a DVD"
    private var rentalMovieList = List[(Int, MovieRental)]()
    private var rentalGameList = List[(Int, GameRental)]()
    contents = new MigPanel("insets 0", "[grow] 0 [grow]","[] 0 [grow] 8 [grow]") {
      
      val dvdlabel = new Label("Enter DVD ID")
      val dvdfield = new TextField(10)
      val customerlabel = new Label("Enter Customer ID")
      val customerfield = new TextField(10)
      val cancelButton = new Button(Action("Cancel"){ close })
      val okButton = new Button("OK")
      val doneButton = new Button("Done")
      contents += (dvdlabel, "growx, span, top")
      contents += (dvdfield, "span, center")
      contents += (customerlabel, "growx, span, top")
      contents += (customerfield, "span, center")
      
      contents += (cancelButton, "center")
      contents += (okButton, "center, wrap")
      contents += (doneButton, "center, wrap")

      centerOnScreen()
      listenTo(okButton)
      listenTo(doneButton)
			
				reactions += {
				case ButtonClicked(`okButton`) =>{
				val cID = customerfield.text.toInt
				val dID = dvdfield.text.toInt
				println("Checking for customer and DVD...")
				val customer = customerTable.get(cID).orNull
				var movie = Sample.movieTitleTable.get(dID).orNull
				var game = Sample.gameTitleTable.get(dID).orNull
				
				if (customer == null) println("Sorry, but this customer is not in the database.")
				else if (movie == null && game == null) println("Sorry, but this DVD is not in the database.")
				else if (movieRentalTable.get(dID).isDefined || gameRentalTable.get(dID).isDefined) println("Sorry, but this DVD is already checked out.")
				else if ((!(movie == null) && movie.copyNumber == 0) || (!(game == null) && game.copyNumber == 0)) println("Sorry, but DVDs with copy numbers of 0 cannot be checked out. Those are the 'display cases'.")
					else {
				  if (game == null) {
				  	val m = new MovieRental(movie.titleID, cID, movie.title, movie.copyNumber, LocalDate.now + Globals.movieRentalPeriod)
				  	rentalMovieList = rentalMovieList ++ List[(Int, MovieRental)]((dID, m))
				  println("Entered in DVD# " + dID + " '" + movie.title +"' #" + movie.titleID)}
				  else {
				  	val g = new GameRental(game.titleID, cID, game.title, game.copyNumber, LocalDate.now + Globals.gameRentalPeriod)
				  	rentalGameList = rentalGameList ++ List[(Int, GameRental)]((dID, g))
				  println("Entered in DVD# " + dID + " '" + game.title +"' #" + game.titleID)
				  }
			  }
				}
	  case ButtonClicked(`doneButton`) => {
	  	for (p <- rentalMovieList) {movieRentalTable.update(p._1, p._2)}
	  	for (p <- rentalGameList) {gameRentalTable.update(p._1, p._2)}
	  	println ("All items successfully checked out. Have a nice day.")
	  	close
	  		}
  		}
		}
  }
				  
		val checkoutAction = Action("Checkout") {
          rentalFrame.visible = true
	       }

	  def returnFrame = new Frame {
    title = "Return a DVD"
    contents = new MigPanel("insets 0", "[grow] 0 [grow]","[] 0 [grow] 8 [grow]") {
	
      val dvdlabel = new Label("Enter DVD ID")
      val dvdfield = new TextField(10)
      
      val cancelButton = new Button(Action("Cancel"){ close })
      val okButton = new Button("OK")
      contents += (dvdlabel, "growx, span, top")
      contents += (dvdfield, "span, center")
      
      contents += (cancelButton, "center")
      contents += (okButton, "center, wrap")

			centerOnScreen()
      listenTo(okButton)

				reactions += {
				case ButtonClicked(`okButton`) =>
				{
					val id = dvdfield.text.toInt
					movieRentalTable.remove(id)
					gameRentalTable.remove(id)
					println("DVD #" + id + " has been returned")
					close
				}
      }
   }
}

				val returnAction = Action("Return DVD") {
          returnFrame.visible = true
	       }
		      
    val newButton = new Button(addCustomerAction)
    val deleteButton = new Button(deleteCustomerAction)
    val checkoutButton = new Button(checkoutAction)
    val returnButton = new Button(returnAction)
    
    val editPanel = new FlowPanel(newButton, deleteButton, checkoutButton, returnButton) {
      maximumSize = new Dimension(800, 12)
    }

    //---------------------------
    // left pane contents
    //---------------------------
    contents += searchPanel
    contents += new ScrollPane(table)
    contents += editPanel
  }
  
  //---------------------------
  // right pane
  //---------------------------

  val customerBox = new BoxPanel(Orientation.Vertical) {

    val details = new GridBagPanel { 
      val idField = new TextField { columns = 10; editable = false }
      val firstNameField = new TextField { columns = 10 }
      val lastNameField = new TextField { columns = 10 }
      val addressField = new TextField { columns = 30 }
      val phoneField = new TextField { columns = 10 }
      val emailField = new TextField { columns = 30}

      val fields = Array(idField, firstNameField, lastNameField, addressField, phoneField, emailField)

      val checkedOutItems = new ComboBox(List[String]()) {
        val emptyModel = ComboBox.newConstantModel(List[String]())
        
        // resets the ComboBox items to those in the input list
        def setItems(items: List[String]) = 
          peer.setModel(ComboBox.newConstantModel(items))
        
        // clears all items
        def clear = peer.setModel(emptyModel)
      }

      val c = new Constraints

      // first row 
      c.gridy = 0
      c.gridx = 0
      c.insets = new Insets(10,0,0,0)
      layout(new Label("Customer Data"){ horizontalAlignment = Alignment.Left }) = c
       
      // second row 
      c.gridy = 1
      c.gridx = 0
      c.insets = new Insets(10,0,0,0)
      layout(new Label("Id:")) = c
       
      c.gridx = 1
      layout(idField) = c

      // third row 
      c.gridy = 2
      c.gridx = 0
      layout(new Label("First Name:")) = c

      c.gridx = 1
      layout(firstNameField) = c

      c.gridx = 2
      c.insets = new Insets(10,0,0,0)
      layout(new Label("Last Name:")) = c

      c.gridx = 3
      c.insets = new Insets(10,0,0,0)
      layout(lastNameField) = c

      // forth row 
      c.gridy = 3
      c.gridx = 0
      layout(new Label("Address:")) = c

      c.gridx = 1
      c.gridwidth = 3
      layout(addressField) = c

      // fifth row 
      c.gridy = 4
      c.gridx = 0
      c.gridwidth = 1
      layout(new Label("Phone:")) = c

      c.gridx = 1
      layout(phoneField) = c

      c.gridx = 2
      layout(new Label("Email:")) = c

      c.gridx = 3
      layout(emailField) = c

      // sixth row 
      c.gridy = 5
      c.gridx = 0
      c.gridwidth = 1
      layout(new Label("Checked out:")) = c

      c.gridx = 1
      layout(checkedOutItems) = c

      listenTo(customerListBox.table.selection)

      def rentedMovies(id: Int) = {
        val selection = movieRentalTable.select(p => p.customerID == id)
        selection.toList.map( e => {
          val (k, r) = e
          "Movie ID: " + r.titleID + " " + r.title + " DVD ID:" + k
        })
      }

      def rentedGames(id: Int) = {
        val selection = gameRentalTable.select(p => p.customerID == id)
        selection.toList.map( e => {
          val (k, r) = e
          "Game ID: " + r.titleID + " " + r.title + " DVD ID:" + k
        })
      }
			
      reactions += {
        case TableRowsSelected(_, _, false) => {
          val t = customerListBox.table
          t.selection.rows.toList match {
            case List(i) =>
              // update all text fields with corresponding values from selected record
	             for (j <- 0 to fields.size-1) fields(j).text = t(i,j).toString	      
              // update combo box with checked out items from selected record 
	             val id = t(i,0).asInstanceOf[Int]
	             val rentedItems = rentedMovies(id) ++ rentedGames(id)
	             checkedOutItems.setItems(rentedItems)
	           case Nil => 
 	             // no selected record
	             // clear all text fields
	             for (j <- 0 to fields.size-1) fields(j).text = ""
	             // clear combo box
	             checkedOutItems.clear
	           case _ => ()
	         }      
	       }
	     }  
    }

    //---------------------------
    // bottom panel
    //---------------------------
    val updateCustomerAction = Action("Update") {
      val t = customerListBox.table
      t.selection.rows.toList match {
        case List(i) => {
 	         // update the selected row with values from corresponding text fields
	         for (j <- 1 to details.fields.size-1)
	         t.update(i, j, details.fields(j).text) 	      
	         // update customerTable
	         val id = t(i,0).asInstanceOf[Int]   
	         val p = Customer(firstName = details.firstNameField.text,
	                          lastName = details.lastNameField.text,
	                          address = details.addressField.text,
	                          phone = details.phoneField.text,
	                          email = details.emailField.text
	                         )
	         println("updating customer table ...")
	         customerTable.update(id, p)
	       }
	       case _ => ()
      }
    }

    val updateButton = new Button(updateCustomerAction)

    val updatePanel = new FlowPanel(updateButton) {
      maximumSize = new Dimension(400, 12)
    }

    //---------------------------
    // right pane contents
    //---------------------------
    contents += details
    contents += updatePanel
  }

  // main pane
  val pane = new SplitPane(Orientation.Vertical, customerListBox, customerBox) {
    continuousLayout = true
  }
}