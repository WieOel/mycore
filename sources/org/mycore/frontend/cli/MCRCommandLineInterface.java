/**
 * $RCSfile$
 * $Revision$ $Date$
 *
 * This file is part of ** M y C o R e **
 * Visit our homepage at http://www.mycore.de/ for details.
 *
 * This program is free software; you can use it, redistribute it
 * and / or modify it under the terms of the GNU General Public License
 * (GPL) as published by the Free Software Foundation; either version 2
 * of the License or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program, normally in the file license.txt.
 * If not, write to the Free Software Foundation Inc.,
 * 59 Temple Place - Suite 330, Boston, MA  02111-1307 USA
 *
 **/

package org.mycore.frontend.cli;

import java.io.*;
import java.text.*;
import java.util.*;
import java.lang.reflect.*;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import org.mycore.common.*;

/**
 * The main class implementing the MyCoRe command line interface. With
 * the command line interface, you can import, export, update and delete
 * documents and other data from/to the filesystem. Metadata is imported
 * from and exported to XML files. The command line interface is for
 * administrative purposes and to be used on the server side. It
 * implements an interactive command prompt and understands a set of commands.
 * Each command is an instance of the class <code>MCRCommand</code>.
 *
 * @see MCRCommand
 *
 * @author Frank Lützenkirchen
 * @author Detlev Degenhardt
 * @version $Revision$ $Date$
 */
public class MCRCommandLineInterface
  {

  /** The Logger **/
  static Logger logger=Logger.getLogger(MCRCommandLineInterface.class.getName());

  /** The configuration **/
  private static MCRConfiguration config = null;

  /** The total number of known commands */
  protected static int numCommands = 0;

  /** The array holding all known commands */
  protected static MCRCommand[] knownCommands = new MCRCommand[ 200 ];

  /** A queue of commands waiting to be executed */
  protected static Vector commandQueue = new Vector();

  /** The standard input console where the user enters commands */
  protected static BufferedReader console = new BufferedReader( new InputStreamReader( System.in ) );

 /**
  * Reads command definitions from a configuration file
  * and builds the MCRCommand instances
  **/
  protected static void initCommands()
    {
    // **************************************
    // Built-in commands
    // **************************************

    knownCommands[ numCommands++ ] = new MCRCommand(
      "process {0}",
      "org.mycore.frontend.cli.MCRCommandLineInterface.readCommandsFile String" );
    knownCommands[ numCommands++ ] = new MCRCommand(
      "help",
      "org.mycore.frontend.cli.MCRCommandLineInterface.listKnownCommands" );
    knownCommands[ numCommands++ ] = new MCRCommand(
      "exit",
      "org.mycore.frontend.cli.MCRCommandLineInterface.exit" );
    knownCommands[ numCommands++ ] = new MCRCommand(
      "quit",
      "org.mycore.frontend.cli.MCRCommandLineInterface.exit" );
    knownCommands[ numCommands++ ] = new MCRCommand(
      "! {0}",
      "org.mycore.frontend.cli.MCRCommandLineInterface.executeShellCommand String" );

    // *************************************************
    // Commands for object management
    // *************************************************

    knownCommands[ numCommands++ ] = new MCRCommand(
      "delete object {0}",
      "org.mycore.frontend.cli.MCRObjectCommands.delete String" );
    knownCommands[ numCommands++ ] = new MCRCommand(
      "delete object from {0} to {1}",
      "org.mycore.frontend.cli.MCRObjectCommands.delete String String" );
    knownCommands[ numCommands++ ] = new MCRCommand(
      "load object from file {0}",
      "org.mycore.frontend.cli.MCRObjectCommands.loadFromFile String" );
    knownCommands[ numCommands++ ] = new MCRCommand(
      "update object from file {0}",
      "org.mycore.frontend.cli.MCRObjectCommands.updateFromFile String" );
    knownCommands[ numCommands++ ] = new MCRCommand(
      "load all objects from directory {0}",
      "org.mycore.frontend.cli.MCRObjectCommands.loadFromDirectory String" );
    knownCommands[ numCommands++ ] = new MCRCommand(
      "update all objects from directory {0}",
      "org.mycore.frontend.cli.MCRObjectCommands.updateFromDirectory String" );
    knownCommands[ numCommands++ ] = new MCRCommand(
      "show object {0}",
      "org.mycore.frontend.cli.MCRObjectCommands.show String" );
    knownCommands[ numCommands++ ] = new MCRCommand(
      "save object {0} to {1}",
      "org.mycore.frontend.cli.MCRObjectCommands.save String String" );
    knownCommands[ numCommands++ ] = new MCRCommand(
      "get next object ID for base {0}",
      "org.mycore.frontend.cli.MCRObjectCommands.getID String" );

    // *************************************************
    // Commands for derivate management
    // *************************************************

    knownCommands[ numCommands++ ] = new MCRCommand(
      "delete derivate {0}",
      "org.mycore.frontend.cli.MCRDerivateCommands.delete String" );
    knownCommands[ numCommands++ ] = new MCRCommand(
      "delete derivate from {0} to {1}",
      "org.mycore.frontend.cli.MCRObjectCommands.delete String String" );
    knownCommands[ numCommands++ ] = new MCRCommand(
      "load derivate from file {0}",
      "org.mycore.frontend.cli.MCRDerivateCommands.loadFromFile String" );
    knownCommands[ numCommands++ ] = new MCRCommand(
      "update derivate from file {0}",
      "org.mycore.frontend.cli.MCRDerivateCommands.updateFromFile String" );
    knownCommands[ numCommands++ ] = new MCRCommand(
      "load all derivates from directory {0}",
      "org.mycore.frontend.cli.MCRDerivateCommands.loadFromDirectory String" );
    knownCommands[ numCommands++ ] = new MCRCommand(
      "update all derivates from directory {0}",
      "org.mycore.frontend.cli.MCRDerivateCommands.updateFromDirectory String" );
    knownCommands[ numCommands++ ] = new MCRCommand(
      "show derivate {0}",
      "org.mycore.frontend.cli.MCRDerivateCommands.show String" );
    knownCommands[ numCommands++ ] = new MCRCommand(
      "save derivate {0}",
      "org.mycore.frontend.cli.MCRDerivateCommands.save String" );
    knownCommands[ numCommands++ ] = new MCRCommand(
      "save derivate {0} to {1}",
      "org.mycore.frontend.cli.MCRDerivateCommands.save String String" );
    knownCommands[ numCommands++ ] = new MCRCommand(
      "get next derivate ID for base {0}",
      "org.mycore.frontend.cli.MCRDerivateCommands.getID String" );

    // **************************************
    // Commands for classification management
    // **************************************

    knownCommands[ numCommands++ ] = new MCRCommand(
      "delete classification {0}",
      "org.mycore.frontend.cli.MCRClassificationCommands.delete String" );
    knownCommands[ numCommands++ ] = new MCRCommand(
      "load classification from file {0}",
      "org.mycore.frontend.cli.MCRClassificationCommands.loadFromFile String" );
    knownCommands[ numCommands++ ] = new MCRCommand(
      "update classification from file {0}",
      "org.mycore.frontend.cli.MCRClassificationCommands.updateFromFile String" );
    knownCommands[ numCommands++ ] = new MCRCommand(
      "load all classifications from directory {0}",
      "org.mycore.frontend.cli.MCRClassificationCommands.loadFromDirectory String" );
    knownCommands[ numCommands++ ] = new MCRCommand(
      "update all classifications from directory {0}",
      "org.mycore.frontend.cli.MCRClassificationCommands.updateFromDirectory String"
      );
    knownCommands[ numCommands++ ] = new MCRCommand(
      "save classification {0} to {1}",
      "org.mycore.frontend.cli.MCRClassificationCommands.save String String" );

    // *************************************
    // Commands for executing configurations
    // *************************************

    knownCommands[ numCommands++ ] = new MCRCommand(
      "create database for {0}",
      "org.mycore.frontend.cli.MCRBaseCommands.createDataBase String" );
    knownCommands[ numCommands++ ] = new MCRCommand(
      "create schema for {0}",
      "org.mycore.frontend.cli.MCRBaseCommands.createXMLSchema String" );

    // ******************************
    // Commands for executing queries
    // ******************************

    knownCommands[ numCommands++ ] = new MCRCommand(
      "query local {0} {1}",
      "org.mycore.frontend.cli.MCRQueryCommands.queryLocal String String" );
    knownCommands[ numCommands++ ] = new MCRCommand(
      "query remote {0} {1}",
      "org.mycore.frontend.cli.MCRQueryCommands.queryRemote String String" );
    knownCommands[ numCommands++ ] = new MCRCommand(
      "query host {0} {1} {2}",
      "org.mycore.frontend.cli.MCRQueryCommands.query String String String" );

    // **************************************
    // Commands for user and group management
    // **************************************

    knownCommands[ numCommands++ ] = new MCRCommand(
      "check user data consistency",
      "org.mycore.frontend.cli.MCRUserCommands.checkConsistency" );
    knownCommands[ numCommands++ ] = new MCRCommand(
      "create user data from file or directory {0}",
      "org.mycore.frontend.cli.MCRUserCommands.createFromFile String" );
    knownCommands[ numCommands++ ] = new MCRCommand(
      "import user data from file or directory {0}",
      "org.mycore.frontend.cli.MCRUserCommands.importFromFile String" );
    knownCommands[ numCommands++ ] = new MCRCommand(
      "update user data from file or directory {0}",
      "org.mycore.frontend.cli.MCRUserCommands.updateFromFile String" );
    knownCommands[ numCommands++ ] = new MCRCommand(
      "delete user {0}",
      "org.mycore.frontend.cli.MCRUserCommands.deleteUser String" );
    knownCommands[ numCommands++ ] = new MCRCommand(
      "delete group {0}",
      "org.mycore.frontend.cli.MCRUserCommands.deleteGroup String" );
    knownCommands[ numCommands++ ] = new MCRCommand(
      "list all users",
      "org.mycore.frontend.cli.MCRUserCommands.listAllUsers" );
    knownCommands[ numCommands++ ] = new MCRCommand(
      "list all groups",
      "org.mycore.frontend.cli.MCRUserCommands.listAllGroups" );
    knownCommands[ numCommands++ ] = new MCRCommand(
      "list all privileges",
      "org.mycore.frontend.cli.MCRUserCommands.listAllPrivileges" );
    knownCommands[ numCommands++ ] = new MCRCommand(
      "save all users to file {0}",
      "org.mycore.frontend.cli.MCRUserCommands.saveAllUsersToFile String" );
    knownCommands[ numCommands++ ] = new MCRCommand(
      "save all groups to file {0}",
      "org.mycore.frontend.cli.MCRUserCommands.saveAllGroupsToFile String" );
    knownCommands[ numCommands++ ] = new MCRCommand(
      "save all privileges to file {0}",
      "org.mycore.frontend.cli.MCRUserCommands.saveAllPrivilegesToFile String" );
    knownCommands[ numCommands++ ] = new MCRCommand(
      "save user {0} to file {1}",
      "org.mycore.frontend.cli.MCRUserCommands.saveUserToFile String String" );
    knownCommands[ numCommands++ ] = new MCRCommand(
      "save group {0} to file {1}",
      "org.mycore.frontend.cli.MCRUserCommands.saveGroupToFile String String" );
    knownCommands[ numCommands++ ] = new MCRCommand(
      "set password for user {0} to {1}",
      "org.mycore.frontend.cli.MCRUserCommands.setPassword String String" );
    knownCommands[ numCommands++ ] = new MCRCommand(
      "set user management to read only mode",
      "org.mycore.frontend.cli.MCRUserCommands.setLock" );
    knownCommands[ numCommands++ ] = new MCRCommand(
      "set user management to read/write mode",
      "org.mycore.frontend.cli.MCRUserCommands.unLock" );
    knownCommands[ numCommands++ ] = new MCRCommand(
      "show user {0}",
      "org.mycore.frontend.cli.MCRUserCommands.showUser String" );
    knownCommands[ numCommands++ ] = new MCRCommand(
      "show group {0}",
      "org.mycore.frontend.cli.MCRUserCommands.showGroup String" );
    }

 /**
  * The main method that either shows up an interactive command prompt or
  * reads a file containing a list of commands to be processed
  */
  public static void main( String[] args )
    {
    config = MCRConfiguration.instance();
    PropertyConfigurator.configure(config.getLoggingProperties());

    logger.info( "" );
    logger.info( "MyCoRe Command Line Interface. Type 'help' to get help!" );
    logger.info( "Initializing: " );

    try{ 
      initCommands(); }
    catch( MCRException ex ) {
      logger.debug( ex.getStackTraceAsString() );
      logger.error( ex.getMessage() );
      logger.error( "" );
      System.exit( 1 );
      }
    logger.info( "Done." );
    logger.info( "" );

    if( args.length > 0 ) {
      StringBuffer cmd = new StringBuffer();
      for( int i = 0; i < args.length; i++ ) {
        cmd.append( args[ i ] ).append( " " ); }
      commandQueue.addElement( cmd.toString().trim() );
      commandQueue.addElement( "exit" );
      }

    String command;
    while( true ) {
      if( commandQueue.isEmpty() ) {
        command = readCommandFromPrompt(); }
      else {
        command = (String) commandQueue.firstElement();
        commandQueue.removeElementAt( 0 );
        }
      processCommand( command );
      }
    }

 /**
  * Shows up a command prompt.
  *
  * @return The command entered by the user at stdin
  */
  protected static String readCommandFromPrompt()
  {
    String line = "";
    do {
      logger.info( "MyCoRe:> " );
      try{ line = console.readLine(); }catch( IOException ex ){}
      }
    while( ( line = line.trim() ).length() == 0 );
    return line;
    }

 /**
  * Processes a command entered by searching a matching command
  * in the list of known commands and executing its method.
  *
  * @param command The command string to be processed
  */
  protected static void processCommand( String command )
    {
    try {
      for( int i = 0; i < numCommands; i++ ) {
        if( knownCommands[ i ].invoke( command ) ) { return; } 
        }
      logger.error( "Command not understood. Enter 'help' to get a list of commands." );
      }
    catch( MCRException ex ) {
      logger.debug( ex.getStackTraceAsString() );
      logger.error( ex.getMessage() );
      logger.error( "" );
      }
    catch( Exception ex ) {
      if( ex instanceof InvocationTargetException ) {
        Throwable t = ( (InvocationTargetException)ex ).getTargetException();
        logger.error(t.toString());
        }
      else {
        logger.error( ex.getMessage() );
        }
      }
    }

 /**
  * Reads a file containing a list of commands to be executed and adds
  * them to the commands queue for processing.  This method implements
  * the "process ..." command.
  *
  * @param file The file holding the commands to be processed
  * @throws IOException when the file could not be read
  * @throws FileNotFoundException when the file was not found
  */
  public static void readCommandsFile( String file )
    throws IOException, FileNotFoundException
    {
    BufferedReader reader = new BufferedReader( new FileReader( file ) );
    logger.info( "Reading commands from file " + file );
    String line;
    int pos = 0;
    while( ( line = reader.readLine() ) != null ) {
      line = line.trim();
      if( line.startsWith( "#" ) || ( line.length() == 0 ) )
        continue;
      else
        commandQueue.insertElementAt( line, pos++ );
      }
    reader.close();
    }

 /**
  * Shows a list of commands understood by the command line interface and
  * shows their input syntax. This method implements the "help" command
  */
  public static void listKnownCommands()
    {
    logger.info( "The following is a list of known commands:\n" );
    for( int i = 0; i < numCommands; i++ ) {
      logger.info(knownCommands[ i ].showSyntax()); }
    }

 /**
  * Executes simple shell commands from inside the command line
  * interface and shows their output. This method implements commands
  * entered beginning with exclamation mark, like "! ls -l /temp"
  *
  * @param command the shell command to be executed
  * @throws IOException when an IO error occured while catching the output returned by the command
  * @throws InterruptedException when the external command execution was interrupted
  * @throws SecurityException when the command could not be executed for security reasons
  */
  public static void executeShellCommand( String command )
    throws IOException, SecurityException, InterruptedException
    {
    Process p = Runtime.getRuntime().exec( command );
    showOutput( p.getInputStream() );
    showOutput( p.getErrorStream() );
    }

 /**
  * Catches the output read from an input stream and prints it line by line
  * on standard out. This is used to catch the stdout and stderr stream output
  * when executing an external shell command.
  */
  protected static void showOutput( InputStream in )
    throws IOException
    {
    int c;
    StringBuffer sb = new StringBuffer(1024);
    while( ( c = in.read() ) != -1 ) {
      sb.append( (char)c ); }
    logger.info(sb.toString());
    }

 /**
  * Exits the command line interface. This method implements the "exit" and
  * "quit" commands.
  */
  public static void exit()
    {
    logger.info( "Goodbye, and remember: \"Alles wird gut.\"\n" );
    System.exit( 0 );
    }
  }

