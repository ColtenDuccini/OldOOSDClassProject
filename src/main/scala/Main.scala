/***********************************************
   22c22: Object Oriented Software Development
   Fall 2012
   The University of Iowa
   
   Instructor: Cesare Tinelli

	Code by Cesare Tinelli, Colten Duccini and Tingting Gao 
************************************************/

import scala.swing._
import event._
import Swing._

import net.miginfocom.swing._
import in.raam.calc._

import org.scala_tools.time.Imports._

object GUI extends SimpleSwingApplication {

	  println(LocalDate.now + 7.days)
	 
  def passwordFrame = new Frame {
    title = "Password"
    contents = new MigPanel("insets 0", "[grow] 0 [grow]","[] 0 [grow] 8 [grow]") {
      val label = new Label("Enter password:")
      val field = new TextField(10)
      val cancelButton = new Button(Action("Cancel"){ close })
      val okButton = new Button("OK")
      contents += (label, "growx, span, top")
      contents += (field, "span, center")
      contents += (cancelButton, "center")
      contents += (okButton, "center, wrap")

      centerOnScreen()
      listenTo(field)
      listenTo(okButton)

      reactions += {
        case EditDone(`field`) => if (field.text == "1234"){Globals.managerMode = true
        println ("Password approved!")}
        case ButtonClicked(`okButton`) => if (field.text == "1234"){Globals.managerMode = true
        println ("checking password")}
      }
    }
  }
  
  val mainFrame = new MainFrame {
    title = "Rentalot DVD Rental System"

    val f = 1
    
    // Main frame menubar 
    menuBar = new MenuBar {
      contents ++= List(mainMenu, taskMenu, windowMenu)
            
      def mainMenu = new Menu(Globals.applName) {
      	
        val aboutAction =	Action("About " + Globals.applName) {
	         Dialog.showMessage(message = "Developed by\n" + Globals.developers) 
	       }
	       val quitAction = Action("Quit " + Globals.applName) {
	         println("closing")
	         Globals.saveAll
	         quit()
	       }
        contents += new MenuItem(aboutAction)
        contents += new Separator
        contents += new MenuItem("Preferences...")
        contents += new Separator
        contents += new MenuItem(quitAction)
      }
              
      def taskMenu = new Menu("Tasks") {
        val enterManagerModeAction = Action("Enter manager Mode") {
          passwordFrame.visible = true
	       }
        val exitManagerModeAction = Action("Exit manager Mode") {
          Globals.managerMode = false
        }
        val saveAction = Action("Save") {
	         println("Saving ...")
	         Globals.saveAll
	         println("Saved")
        }
        contents += new MenuItem(enterManagerModeAction)
        contents += new MenuItem(exitManagerModeAction)
        contents += new Separator
        contents += new MenuItem(saveAction)
      }

      def windowMenu = new Menu("Window") {
        val minimizeAction = Action("Minimize") {
          println("Minimizing ...")
        }
        val zoomAction = Action("Zoom") {
          println("Zooming ...")
        }
        contents += new MenuItem(minimizeAction)
        contents += new Separator
        contents += new MenuItem(zoomAction)
      }
    }

    // Main frame contents
    
    val borderPanel = new BorderPanel {
      import BorderPanel.Position._

      var reactLive = false

      // Tabbed pane 
      val tabs = new TabbedPane {
        import TabbedPane._

        // Customers pane built with sample data from Sample module
        // It should be replaced with data from actual database
        val customersUI = new CustomersUI(Sample.customerTable, Sample.movieRentalTable, Sample.gameRentalTable)
        val customersPane = customersUI.pane
        
        // Movies pane should go here.
        // Could be created by a class similar to CustomersUI
        
        val moviesUI = new MoviesUI(Sample.customerTable, Sample.movieRentalTable, Sample.movieTitleTable)
        val moviesPane = moviesUI.pane
        

        // Games pane, should go here.
        // Could be created by a class similar to CustomersUI
        val gamesUI = new GamesUI(Sample.customerTable, Sample.gameRentalTable, Sample.gameTitleTable)
        val gamesPane = gamesUI.pane

        // Tabbed pane contents
        pages += new Page("Customers", customersPane)
        pages += new Page("Movies", moviesPane) 
        pages += new Page("Games", gamesPane) 
      }
      layout(tabs) = Center 
    }
    
    contents = borderPanel
  }
  
  def top = mainFrame
}





		