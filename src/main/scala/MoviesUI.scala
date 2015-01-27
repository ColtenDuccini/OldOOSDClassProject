import scala.swing._
import event._
import Swing._
import javax.swing.table._

import net.miginfocom.swing._
import in.raam.calc._

import BorderPanel.Position._
import TabbedPane._
import GridBagPanel._


class MoviesUI(customerTable: CustomerPTable, movieRentalTable: MovieRentalPTable, movieTitleTable: MovieTitlePTable) {

  // column names for the UI table
  val columnNames = Array[AnyRef]("Item ID", "Movie Title ID","Title","Director","Year","Rating","Copy #")

  //---------------------------
  // left pane
  //---------------------------
  val movieListBox = new BoxPanel(Orientation.Vertical) {

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
    val table = new Table(movieTitleTable.size, columnNames.size) {
      model = tableModel
      preferredViewportSize = new Dimension(500, 50)
      selection.intervalMode = Table.IntervalMode.Single
      selection.elementMode = Table.ElementMode.Row
    }

    for ((itemid, MovieTitle(titleID, title, director, year, rating, copyNumber )) <- movieTitleTable.toList) {
      val row = Array[AnyRef](itemid: java.lang.Integer, titleID:java.lang.Integer, title:java.lang.String, director:java.lang.String, year:java.lang.Integer, rating:java.lang.String, copyNumber:java.lang.Integer)
      tableModel.addRow(row)
    }

    table.peer.setColumnSelectionAllowed(false)
	   table.peer.setRowSelectionAllowed(true)

    // Select row 2
    table.peer.setRowSelectionInterval(0, 0)

    //---------------------------
    // bottom panel
    //---------------------------
		def newMovieFrame = new Frame {
    title = "Add New Movie Title"
    contents = new MigPanel("insets 0", "[grow] 0 [grow]","[] 0 [grow] 8 [grow]") {
      val titlelabel = new Label("Enter movie title:")
      val titlefield = new TextField(10)
      val directorlabel = new Label("Enter name of director:")
      val directorfield = new TextField(10)
      val yearlabel = new Label("Enter year of release:")
      val yearfield = new TextField(10)
      val ratinglabel = new Label("Enter movie rating:")
      val ratingfield = new TextField(10)
      
      val cancelButton = new Button(Action("Cancel"){ close })
      val okButton = new Button("OK")
      contents += (titlelabel, "growx, span, top")
      contents += (titlefield, "span, center")
      contents += (directorlabel, "growx, span, top")
      contents += (directorfield, "span, center")
      contents += (yearlabel, "growx, span, top")
      contents += (yearfield, "span, center")
      contents += (ratinglabel, "growx, span, top")
      contents += (ratingfield, "span, center")
      
      contents += (cancelButton, "center")
      contents += (okButton, "center, wrap")

      centerOnScreen()
      listenTo(okButton)

	reactions += {
        case ButtonClicked(`okButton`) => {
        //Generate new MovieTitle with inputed information
				val newmovieID = Globals.getNewTitleId
        val newdvdID = Globals.getNewDVDId
				val newmovie = new MovieTitle(newmovieID, titlefield.text.toString, directorfield.text.toString, yearfield.text.toInt, ratingfield.text.toString, 0)
			// Create new row in table
			val newRow = Array[AnyRef](newdvdID:java.lang.Integer, newmovieID:java.lang.Integer, newmovie.title, newmovie.director, newmovie.year:java.lang.Integer, newmovie.rating, newmovie.copyNumber:java.lang.Integer)
			tableModel.insertRow(0, newRow)
			movieTitleTable.update(newdvdID, newmovie)
			println("New title added to database")}
			close
		}
	}
}
val addMovieAction = Action("Add New Movie Title") {
    	if (Globals.managerMode == false) {println("Must be in manager mode to add new movie titles")}
    	else
    	{
          newMovieFrame.visible = true
      }
    }
		
    def newDVDFrame = new Frame {
    title = "Add New DVD"
    contents = new MigPanel("insets 0", "[grow] 0 [grow]","[] 0 [grow] 8 [grow]") {
      val titlelabel = new Label("Enter movie title:")
      val titlefield = new TextField(10)
      
      val cancelButton = new Button(Action("Cancel"){ close })
      val okButton = new Button("OK")
      contents += (titlelabel, "growx, span, top")
      contents += (titlefield, "span, center")
      
      contents += (cancelButton, "center")
      contents += (okButton, "center, wrap")

      centerOnScreen()
      listenTo(okButton)

	reactions += {
        case ButtonClicked(`okButton`) => {
        //Search to see if given movie title is already in the database
        val selection = movieTitleTable.select(p => p.title == titlefield.text)
        //If not, do nothing.
				if (selection.isEmpty == true) {
				println("That title is not in the database.")
			}
			else
			{
				val newdvdID = Globals.getNewDVDId
				val titleEntry = selection.toList.head._2
				val newDVD = new MovieTitle(titleEntry.titleID, titleEntry.title, titleEntry.director, 	titleEntry.year, titleEntry.rating, (selection.size))
			// Create new row in table
			val newRow = Array[AnyRef](newdvdID:java.lang.Integer, newDVD.titleID:java.lang.Integer, newDVD.title, newDVD.director, newDVD.year:java.lang.Integer, newDVD.rating, newDVD.copyNumber:java.lang.Integer)
			tableModel.insertRow(0, newRow)
			movieTitleTable.update(newdvdID, newDVD)
			println("New DVD added to database")
			close}
      }
		}
	}
}

				val addDVDAction = Action("Add New DVD") {
    	if (Globals.managerMode == false) {println("Must be in manager mode to add new DVDs")}
    	else
    	{
    	newDVDFrame.visible = true
    	}
    }

    val deleteDVDAction = Action("Delete DVD") {
      if (Globals.managerMode == false) {println("Must be in manager mode to delete DVDs")}
      else {
      table.selection.cells.toList match {
        // delete only singly selected rows
        case List((i, _)) => {
        	val id = table(i,0).asInstanceOf[Int]
        	if (movieRentalTable.get(id).isEmpty == false) {println("Cannot delete a DVD that is checked out.")}
        	else { 
          tableModel.removeRow(i)
          println("Deleting DVD # " + id)
          movieTitleTable.remove(id)
        	}
        }
        case _ => () 
      }
      }
    }

		//Deletes all movie DVDs with the same title ID as the selected movie.
    val deleteMovieAction = Action("Delete Movie Title") {
      if (Globals.managerMode == false) {println("Must be in manager mode to delete movie title.")}
      else {
      table.selection.cells.toList match {
        case List((i, _)) => {
        	val titleid = table(i,1).asInstanceOf[Int]
        	if (movieRentalTable.get(titleid).isEmpty == false) {println("Cannot delete a title that has DVDs checked out.")}
        	else {
    
		val dvds = movieTitleTable.select(p => p.titleID == titleid).toList
		val title = dvds.head._2.title
		for ((k,r) <- dvds)
		{movieTitleTable.remove(k)
        	}
		println("All copies of " + title + " have been deleted.")
        }
        }
        case _ => () 
      }
    }
  }

    val newButton = new Button(addMovieAction)
    val newDVDButton = new Button(addDVDAction)
    val newDVDRemoveButton = new Button(deleteDVDAction)
    val deleteButton = new Button(deleteMovieAction)

    val editPanel = new FlowPanel(newButton, newDVDButton, newDVDRemoveButton, deleteButton) {
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

  val movieBox = new BoxPanel(Orientation.Vertical) {

    val details = new GridBagPanel { 
    	val itemidField = new TextField {columns = 12; editable = false}
      val titleIDField = new TextField { columns = 12; editable = false }
      val titleField = new TextField { columns = 12; editable = false }
      val directorField = new TextField {columns = 12; editable = false}
      val yearField = new TextField {columns = 12; editable = false}
      val ratingField = new TextField {columns = 12; editable = false}
      val copynumberField = new TextField {columns = 12; editable = false}
      val availableField = new TextField {columns = 12; editable = false}
      val customerIDField = new TextField {columns = 12; editable = false}
      val duedateField = new TextField {columns = 12; editable = false}

      val fields = Array(itemidField, titleIDField, titleField, directorField, yearField, ratingField, copynumberField, availableField, customerIDField, duedateField)

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
      layout(new Label("Movie Inventory Data"){ horizontalAlignment = Alignment.Left }) = c
       
      // second row 
      c.gridy = 1
      c.gridx = 0
      c.insets = new Insets(10,0,0,0)
      layout(new Label("DVD ID:")) = c
       
      c.gridx = 1
      layout(itemidField) = c


      c.gridx = 2
      c.insets = new Insets(10,0,0,0)
      layout(new Label("Movie ID:")) = c

      c.gridx = 3
      layout(titleIDField) = c

			c.gridy = 2
      c.gridx = 0
      c.insets = new Insets(10,0,0,0)
      layout(new Label("Title:")) = c

      c.gridx = 1
      layout(titleField) = c

      c.gridx = 2
      layout(new Label("Director:")) = c

      c.gridx = 3
      layout(directorField) = c

			c.gridy = 3
			c.gridx = 0
			layout(new Label("Year:")) = c

			c.gridx = 1
			c.gridwidth = 1
			layout(yearField) = c
			
      c.gridx = 2
      c.gridwidth = 1
      layout(new Label("Rating:")) = c

      c.gridx = 3
      layout(ratingField) = c

      c.gridy = 4
			c.gridx = 0
			layout(new Label("Copy #:")) = c

			c.gridx = 1
			c.gridwidth = 1
			layout(copynumberField) = c

			c.gridx = 2
			layout(new Label("Available?")) = c

			c.gridx = 3
			layout(availableField) = c

			c.gridy = 5
			c.gridx = 0
			layout(new Label("Customer ID:")) = c

			c.gridx = 1
			c.gridwidth = 1
			layout(customerIDField) = c

			c.gridx = 2
			layout(new Label("Due date:")) = c

			c.gridx = 3
			layout(duedateField) = c

      listenTo(movieListBox.table.selection)

      reactions += {
        case TableRowsSelected(_, _, false) => {
          val t = movieListBox.table
          t.selection.rows.toList match {
            case List(i) => {
              // update all text fields with corresponding values from selected record
	             for (j <- 0 to 6) fields(j).text = t(i,j).toString
	             val id = fields(0).text.toInt
	             val rental = movieRentalTable.get(id)
	             rental match {
	             	case Some(r) => { fields(7).text = "No" 
	             	fields(8).text = r.customerID.toString
	             	fields(9).text = r.duedate.toString
	             }
	             case None => {fields(7).text = "Yes"
	             fields(8).text = ""
	             fields(9).text = ""
	             }
	             }
            }
	           case Nil => 
 	             // no selected record
	             // clear all text fields
	             for (j <- 0 to fields.size-1) fields(j).text = ""
	           case _ => ()
            }  
	       }
	     }  
    }
    
    //---------------------------
    // right pane contents
    //---------------------------
    contents += details
  }

  // main pane
  val pane = new SplitPane(Orientation.Vertical, movieListBox, movieBox) {
    continuousLayout = true
  }
}
	