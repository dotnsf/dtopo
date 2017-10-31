//. topo.java

package me.juge.dtopo;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Event;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;


public class topo{
  static int scn_w0, scn_w1;
  static int scn_h0, scn_h1;

  public static void main( String[] args ){
    int i = 0, j;
    int defMS;
    String sic0 = "";
    String sic1 = "";
    String cic0 = "";
    String cic1 = "";
    String nic0 = "";
    String nic1 = "";


    try{
      BufferedReader br = new BufferedReader( new FileReader( "dtopo0.dat" ) );
      String line;
      boolean b = true;

      while( ( line = br.readLine() ) != null && b ){
        if( line.length() == 0 ){
          b = false;
        }else{
          if( line.startsWith( "sw: " ) && line.length() > 4 ){
            scn_w0 = Integer.parseInt( line.substring( 4 ) );
          }else if( line.startsWith( "sh: " ) && line.length() > 4 ){
            scn_h0 = Integer.parseInt( line.substring( 4 ) );
          }else if( line.startsWith( "ic: " ) && line.length() > 4 ){
            sic0 = line.substring( 4 );
          }else if( line.startsWith( "sic: " ) && line.length() > 5 ){
            sic0 = line.substring( 5 );
          }else if( line.startsWith( "cic: " ) && line.length() > 5 ){
            cic0 = line.substring( 5 );
          }else if( line.startsWith( "nic: " ) && line.length() > 5 ){
            nic0 = line.substring( 5 );
          }
        }
      }

      br.close();

      br = new BufferedReader( new FileReader( "dtopo1.dat" ) );
      b = true;

      while( ( line = br.readLine() ) != null && b ){
        if( line.length() == 0 ){
          b = false;
        }else{
          if( line.startsWith( "sw: " ) && line.length() > 4 ){
            scn_w1 = Integer.parseInt( line.substring( 4 ) );
          }else if( line.startsWith( "sh: " ) && line.length() > 4 ){
            scn_h1 = Integer.parseInt( line.substring( 4 ) );
          }else if( line.startsWith( "ic: " ) && line.length() > 4 ){
            sic1 = line.substring( 4 );
          }else if( line.startsWith( "sic: " ) && line.length() > 5 ){
            sic1 = line.substring( 5 );
          }else if( line.startsWith( "cic: " ) && line.length() > 5 ){
            cic1 = line.substring( 5 );
          }else if( line.startsWith( "nic: " ) && line.length() > 5 ){
            nic1 = line.substring( 5 );
          }
        }
      }

      br.close();
    }catch( IOException e ){
      System.out.println( "データファイルが見つかりません。" );
      System.exit( 1 );
    }

    try{
      cnvFrame0 mf = new cnvFrame0( scn_w0, scn_h0, sic0, cic0, nic0, 0 );
      cnvFrame0 rf = new cnvFrame0( scn_w1, scn_h1, sic1, cic1, nic1, 1 );

      mf.setTitle( "NOTES/DOMINO MAIL TOPOLOGY" );
      mf.setBackground( Color.white );
      mf.addWindowListener( new appCloser() );
      mf.pack();
      mf.setSize( scn_w0, scn_h0 );
      mf.show();

      rf.setTitle( "NOTES/DOMINO REPLICATION TOPOLOGY" );
      rf.setBackground( Color.white );
      rf.addWindowListener( new appCloser() );
      rf.pack();
      rf.setSize( scn_w1, scn_h1 );
      rf.setBounds( 20, 20, scn_w1, scn_h1 );
      rf.show();

    }catch( Throwable t ){
      System.out.println( "( topo.main() ) uncaught exception: " + t );
      t.printStackTrace();
    }
  }

  protected static final class appCloser extends WindowAdapter{
    public void windowClosing( WindowEvent e ){
      System.exit( 0 );
    }
  }
}

class cnvFrame0 extends Frame{
  cnvFrame0( int sw, int sh, String sic, String cic, String nic, int type ){
    super( "topo" );

    iCanvas0 cnv = new iCanvas0( sw, sh, sic, cic, nic, type );
    add( "Center", cnv );
  }
}

class iCanvas0 extends Canvas{
  Vector lst1 = new Vector();
  Vector lst2 = new Vector();
  Vector srvlst = new Vector();
  Vector pc_cl = new Vector();
  Vector rlstv = new Vector();
  Vector xlstv = new Vector();
  Vector ylstv = new Vector();
  int rlst[];
  int xlst[];
  int ylst[];
  int defMS;
  int defTYPE;
  int scn_w, scn_h;
  String defSICON;
  String defCICON;
  String defNICON;
  double rs;
  int flag = -1;
  int activeIndex;
  Image sicon = null;
  Image cicon = null;
  Image nicon = null;
  boolean iconed = false;

  iCanvas0( int sw, int sh, String sic, String cic, String nic, int type ){
    scn_w = sw;
    scn_h = sh;
    defSICON = sic;
    defCICON = cic;
    defNICON = nic;
    defTYPE = type;  //. 0:メール　1:レプリカ
    int i = 0, j;


    defMS = 0;

    try{
      BufferedReader br = new BufferedReader( new FileReader( "dtopo" + type + ".dat" ) );
      String line;
      int status;

      status = 0;
      while( ( line = br.readLine() ) != null ){
        if( status == 0 ){
          if( line.length() == 0 ){
            status = 1;
            i = 0;
          }
        }else if( status == 1 ){
          if( line.length() == 0 ){
            status = 2;
            defMS = i;

            xlst = new int[defMS];
            ylst = new int[defMS];

            for( i = 0; i < defMS; i ++ ){
              xlst[i] = Integer.parseInt( ( String )xlstv.get( i ) );
              ylst[i] = Integer.parseInt( ( String )ylstv.get( i ) );
            }

            i = 0;
          }else{
            if( line.startsWith( "srv: " ) && line.length() > 5 ){
              srvlst.add( line.substring( 5 ) );
            }else if( line.startsWith( "x: " ) && line.length() > 3 ){
              xlstv.add( line.substring( 3 ) );
            }else if( line.startsWith( "y: " ) && line.length() > 3 ){
              ylstv.add( line.substring( 3 ) );
            }else if( line.startsWith( "pc_cl: " ) && line.length() > 7 ){
              pc_cl.add( line.substring( 7 ) );
              i ++;
            }
          }
        }else if( status == 2 ){
          if( line.startsWith( "src: " ) && line.length() > 5 ){
            lst1.add( line.substring( 5 ) );
          }else if( line.startsWith( "dst: " ) && line.length() > 5 ){
            lst2.add( line.substring( 5 ) );
          }else if( line.startsWith( "route: " ) && line.length() > 7 ){
            rlstv.add( line.substring( 7 ) );
            i ++;
          }
        }
      }

      br.close();
    }catch( IOException e ){
      //. ini ファイルが見つからない場合はエラーとは見なさない・・
    }

    rlst = new int[i];
    for( j = 0; j < i; j ++ ){
      rlst[j] = Integer.parseInt( ( String )rlstv.get( j ) );
    }

    try{
      FileReader fr = new FileReader( defSICON );

      sicon = Toolkit.getDefaultToolkit().getImage( defSICON );
      fr.close();

      if( defCICON.length() == 0 ){
        defCICON = defSICON;
      }
      fr = new FileReader( defCICON );
      cicon = Toolkit.getDefaultToolkit().getImage( defCICON );
      fr.close();

      if( defNICON.length() == 0 ){
        defNICON = defSICON;
      }
      fr = new FileReader( defNICON );
      nicon = Toolkit.getDefaultToolkit().getImage( defNICON );
      fr.close();

      MediaTracker mt = new MediaTracker( this );
      mt.addImage( sicon, 0 );
      mt.addImage( cicon, 0 );
      mt.addImage( nicon, 0 );
      mt.waitForID( 0 );

      iconed = true;
    }catch( IOException e ){
    }catch( InterruptedException e ){
    }

    repaint();
    flag = 0;
  }

  public void paint( Graphics g ){
    int i, j, m, n, s;
    int x1, x2, y1, y2;
    double t;
    String line1, line2, line;
    int c;

    int sw = size().width;
    int sh = size().height;

    Color ct[] = { Color.blue, Color.red, Color.green, Color.orange, Color.gray, Color.black };
    String typename[] = { "", "", "", "", "" };
    if( defTYPE == 0 ){
      typename[0] = "Push Wait"; typename[1] = "Pull-Push"; typename[2] = "Pull Only"; typename[3] = "Push Only"; typename[4] = "SMTP";
      ct[4] = Color.cyan;
    }else if( defTYPE == 1 ){
      typename[0] = "Pull-Pull"; typename[1] = "Pull-Push"; typename[2] = "Pull Only"; typename[3] = "Push Only"; typename[4] = "Disabled";
    }

    //. キャンバスを元通りに
    g.setColor( Color.white );
    g.fillRect( 0, 0, size().width, size().height );

    n = lst1.size();
    m = srvlst.size();

    //. サーバーのアイコンを表示
    if( iconed ){
      for( j = 0; j < m; j ++ ){
        s = Integer.parseInt( ( String )pc_cl.get( j ) );
        if( s == 1 ){
          g.drawImage( sicon, xlst[j], ylst[j], Color.white, null );
        }else if( s == 0 ){
          g.drawImage( cicon, xlst[j], ylst[j], Color.white, null );
        }else{
          g.drawImage( nicon, xlst[j], ylst[j], Color.white, null );
        }
      }
    }

    //. トポロジー図描画
    for( i = 0; i < n; i ++ ){
      line1 = ( ( String )lst1.get( i ) ).toUpperCase();
      line2 = ( ( String )lst2.get( i ) ).toUpperCase();
      c = rlst[i];

      x1 = x2 = y1 = y2 = 0;
      for( j = 0; j < m; j ++ ){
        line = ( ( String )srvlst.get( j ) ).toUpperCase();
        if( line.equals( line1 ) ){
          x1 = xlst[j]; y1 = ylst[j];
        }
        if( line.equals( line2 ) ){
          x2 = xlst[j]; y2 = ylst[j];
        }
      }

//.      drawArrow( g, ct[c], x1, y1, x2, y2 );
      drawArrow( g, ct[c], x1 + c + 2, y1 + c + 2, x2 + c + 2, y2 + c + 2 );
     }

    //. サーバ名表示
    g.setColor( Color.black );
    for( j = 0; j < m; j ++ ){
      line1 = ( String )srvlst.get( j );
      g.drawString( line1, xlst[j] + 2, ylst[j] + 2 );
    }

    //. インフォメーション表示
    drawArrow( g, ct[0], 5, ( int )( sh / 2 ), 40, ( int )( sh / 2 ) );
    g.setColor( ct[0] );
    g.drawString( typename[0], 45, ( int )( sh / 2 ) + 5 );

    drawArrow( g, ct[1], 5, ( int )( sh / 2 ) + 10, 40, ( int )( sh / 2 ) + 10 );
    g.setColor( ct[1] );
    g.drawString( typename[1], 45, ( int )( sh / 2 ) + 15 );

    drawArrow( g, ct[2], 5, ( int )( sh / 2 ) + 20, 40, ( int )( sh / 2 ) + 20 );
    g.setColor( ct[2] );
    g.drawString( typename[2], 45, ( int )( sh / 2 ) + 25 );

    drawArrow( g, ct[3], 5, ( int )( sh / 2 ) + 30, 40, ( int )( sh / 2 ) + 30 );
    g.setColor( ct[3] );
    g.drawString( typename[3], 45, ( int )( sh / 2 ) + 35 );

    if( defTYPE == 0 ){
      drawArrow( g, ct[4], 5, ( int )( sh / 2 ) + 40, 40, ( int )( sh / 2 ) + 40 );
      g.setColor( ct[4] );
      g.drawString( typename[4], 45, ( int )( sh / 2 ) + 45 );
    }

//.    g.setColor( Color.magenta );
//.    g.setFont( new Font( "SansSerif", Font.BOLD, 12 ) );
//.    g.drawString( "LPS @ Lotus Development Japan, all rights reserved.", sw - 300, sh - 5 );
  }

  public void drawArrow( Graphics g, Color c, int x1, int y1, int x2, int y2 ){
    boolean b = false;
    int tmp;
    double t, t1, t2;
    int xp[], yp[];
    int m1, m2, n1, n2;

    int headsize = 8;
    double headrad = Math.PI / 6;  //. = 15°

    g.setColor( c );

    //. 直線部分
    g.drawLine( x1, y1, x2, y2 );

    //. 矢尻の部分
//    g.fillOval( x2 - 2, y2 - 2, 5, 5 );
    xp = new int[3]; yp = new int[3];
    xp[0] = x2; yp[0] = y2;
    m1 = x1; m2 = x2; n1 = y1; n2 = y2;
    if( m1 > m2 ){
      b = true;

      tmp = m1; m1 = m2; m2 = tmp;
      tmp = n1; n1 = n2; n2 = tmp;
    }

    if( m1 == m2 ){
      t = Math.PI / 2;
      if( n2 < n1 ){
        t = t * ( -1.0 );
      }
    }else{
      t = Math.atan( ( n2 - n1 ) / ( m2 - m1 ) );
      if( b ){
        t += Math.PI;
      }
    }

    t += Math.PI;
    t1 = t + headrad;
    t2 = t - headrad;

    xp[1] = x2 + ( int )( headsize * Math.cos( t1 ) );
    yp[1] = y2 + ( int )( headsize * Math.sin( t1 ) );
    xp[2] = x2 + ( int )( headsize * Math.cos( t2 ) );
    yp[2] = y2 + ( int )( headsize * Math.sin( t2 ) );

    g.fillPolygon( xp, yp, 3 );
  }

  public boolean keyDown( Event evt, int key ){
    int i, m, n;

    if( ( key == 1005 ) && flag > -1 ){  //. 'DOWN'
      String tmps;

      tmps = ( String )srvlst.get( 0 );
      srvlst.removeElementAt( 0 );
      srvlst.add( tmps );
	  
	  tmps = ( String )pc_cl.get( 0 );
	  pc_cl.removeElementAt( 0 );
	  pc_cl.add( tmps );

      repaint();
    }
    if( ( key == 1004 ) && flag > -1 ){  //. 'UP'
      String tmps;
      m = srvlst.size();

      tmps = ( String )srvlst.get( m-1 );
      srvlst.removeElementAt( m-1 );
      srvlst.insertElementAt( tmps, 0 );
	  
	  tmps = ( String )pc_cl.get( m - 1 );
	  pc_cl.removeElementAt( m-1 );
	  pc_cl.insertElementAt( tmps, 0 );

      repaint();
    }

    if( ( key == 83 || key == 115 ) && flag > -1 ){  //. 's' or 'S'
      try{
        PrintWriter pw = new PrintWriter( new BufferedWriter( new FileWriter( "dtopo" + defTYPE + ".dat" ) ) );

        pw.println( "sw: " + scn_w );
        pw.println( "sh: " + scn_h );
        pw.println( "sic: " + defSICON );
        pw.println( "cic: " + defCICON );
        pw.println( "nic: " + defNICON );
        pw.println( "" );

        m = srvlst.size();
        for( i = 0; i < m; i ++ ){
          pw.println( "srv: " + ( String )srvlst.get( i ) );
          pw.println( "x: " + xlst[i] );
          pw.println( "y: " + ylst[i] );
          pw.println( "pc_cl: " + ( String )pc_cl.get( i ) );
        }
        pw.println( "" );

        n = lst1.size();
        for( i = 0; i < n; i ++ ){
          pw.println( "src: " + ( String )lst1.get( i ) );
          pw.println( "dst: " + ( String )lst2.get( i ) );
          pw.println( "route: " + rlst[i] );
        }

        pw.close();
      }catch( IOException e ){
        //. ini ファイルが見つからない場合はエラーとは見なさない・・
      }catch( Exception e ){
      }
    }

    return( true );
  }

  public boolean mouseDown( Event e, int x, int y ){
    int i, m;
    double d;

    if( flag == 0 ){
      m = srvlst.size();
      activeIndex = -1;
      for( i = 0; i < m && flag == 0; i ++ ){
        d = Math.pow( ( double )( x - xlst[i] ), 2.0 )
          + Math.pow( ( double )( y - ylst[i] ), 2.0 );
        if( d < 100.0 ){
          activeIndex = i;
          flag = 1;
        }
      }
    }

    return( true );
  }

  public boolean mouseDrag( Event e, int x, int y ){
    if( flag == 1 ){
      xlst[activeIndex] = x;
      ylst[activeIndex] = y;

      repaint();
    }

    return( true );
  }

  public boolean mouseUp( Event e, int x, int y ){
    if( flag == 1 ){
      flag = 0;
    }

    return( true );
  }
}
