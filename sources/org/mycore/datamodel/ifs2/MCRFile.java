package org.mycore.datamodel.ifs2;

import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.VFS;
import org.jdom.Element;

public class MCRFile extends MCRStoredNode
{
  protected MCRFileContent content;
  protected String md5;
  
  protected MCRFile( MCRDirectory parent, FileObject fo ) throws Exception
  { 
    super( parent, fo ); 
    content = new MCRFileContent( this );
    parent.readChildData( this );
  }
  
  public MCRFile( MCRDirectory parent, String name ) throws Exception
  { 
    super( parent, VFS.getManager().resolveFile( parent.fo, name ) );
    md5 = "d41d8cd98f00b204e9800998ecf8427e"; // md5 of empty file
    content = new MCRFileContent( this );
    fo.createFile();
    updateMetadata();
  }
  
  protected MCRNode buildChildNode( FileObject fo ) throws Exception
  { return new MCRVirtualNode( this, fo ); }
  
  protected void writeChildData( Element entry ) throws Exception
  {
    super.writeChildData( entry );
    entry.setAttribute( "md5", this.getMD5() );
    entry.setAttribute( "size", String.valueOf( this.getSize() ) );
  }
  
  protected void readChildData( Element entry ) throws Exception
  { md5 = entry.getAttributeValue( "md5" ); }
  
  public void delete() throws Exception
  {
    super.delete();
    fo.delete(); 
  }
  
  public void setLastModified( long time ) throws Exception
  { 
    fo.getContent().setLastModifiedTime( time );
    updateMetadata();
  }
  
  public String getMD5()
  { return md5; } 

  void setMD5( String md5 )
  { this.md5 = md5; } 

  public MCRContent getContent()
  { return content; }
}
