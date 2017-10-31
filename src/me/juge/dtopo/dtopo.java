//. dtopo.java

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

import lotus.notes.Database;
import lotus.notes.Document;
import lotus.notes.DocumentCollection;
import lotus.notes.NotesException;
import lotus.notes.NotesThread;
import lotus.notes.Session;


public class dtopo{
  static int scn_w = 600;
  static int scn_h = 400;

  public static void main( String[] args ){
    int i;
    String defSERVER = "";
    String defBASE = "";
    String defNAB = "names.nsf";
    boolean defCN = false;
    boolean defID = false;
    boolean defONLYDOMMAIL = false;
    boolean defONLYSMTPMAIL = false;
    String defSICON = "";
    String defCICON = "";
    String defNICON = "";
    int defMS = 150;
    Vector defDOMFILT = new Vector();


    try{
      BufferedReader br = new BufferedReader( new FileReader( "dtopo.ini" ) );
      String line;
      String tmp;

      while( ( line = br.readLine() ) != null ){
        if( !line.startsWith( ";" ) ){
          if( line.startsWith( "s: " ) && line.length() > 3 ){
            defSERVER = line.substring( 3 );
            defBASE = "";
          }else if( line.startsWith( "b: " ) && line.length() > 3 ){
            defBASE = line.substring( 3 );
            defSERVER = "";
          }else if( line.startsWith( "n: " ) && line.length() > 3 ){
            defNAB = line.substring( 3 );
          }else if( line.startsWith( "ic: " ) && line.length() > 4 ){
            defSICON = line.substring( 4 );
          }else if( line.startsWith( "sic: " ) && line.length() > 5 ){
            defSICON = line.substring( 5 );
          }else if( line.startsWith( "cic: " ) && line.length() > 5 ){
            defCICON = line.substring( 5 );
          }else if( line.startsWith( "nic: " ) && line.length() > 5 ){
            defNICON = line.substring( 5 );
          }else if( line.startsWith( "c:" ) ){
            defCN = true;
          }else if( line.startsWith( "id:" ) ){
            defID = true;
          }else if( line.startsWith( "od:" ) ){
            defONLYDOMMAIL = true;
          }else if( line.startsWith( "os:" ) ){
            defONLYSMTPMAIL = true;
          }else if( line.startsWith( "ms: " ) && line.length() > 4 ){
            defMS = Integer.parseInt( line.substring( 4 ) );
          }else if( line.startsWith( "sw: " ) && line.length() > 4 ){
            scn_w = Integer.parseInt( line.substring( 4 ) );
          }else if( line.startsWith( "sh: " ) && line.length() > 4 ){
            scn_h = Integer.parseInt( line.substring( 4 ) );
          }else if( line.startsWith( "df: " ) && line.length() > 4 ){
            defDOMFILT.add( line.substring( 4 ) );
          }
        }
      }

      br.close();
    }catch( IOException e ){
      //. ini ファイルが見つからない場合はエラーとは見なさない・・
    }

    for( i = 0; i < args.length; i ++ ){
      if( args[i].charAt( 0 ) == '-' ){
        if( args[i].substring( 1 ).equals( "s" ) && i + 1 < args.length ){
          //. Server
          defSERVER = args[++i];
          defBASE = "";
        }else if( args[i].substring( 1 ).equals( "b" ) && i + 1 < args.length ){
          //. Base
          defBASE = args[++i];
          defSERVER = "";
        }else if( args[i].substring( 1 ).equals( "n" ) && i + 1 < args.length ){
          //. NAB
          defNAB = args[++i];
        }else if( args[i].substring( 1 ).equals( "ic" ) && i + 1 < args.length ){
          //. Server ICON
          defSICON = args[++i];
        }else if( args[i].substring( 1 ).equals( "sic" ) && i + 1 < args.length ){
          //. Server ICON
          defSICON = args[++i];
        }else if( args[i].substring( 1 ).equals( "cic" ) && i + 1 < args.length ){
          //. Cluster ICON
          defCICON = args[++i];
        }else if( args[i].substring( 1 ).equals( "nic" ) && i + 1 < args.length ){
          //. Network ICON
          defNICON = args[++i];
        }else if( args[i].substring( 1 ).equals( "c" ) ){
          //. CANONICAL
          defCN = true;
        }else if( args[i].substring( 1 ).equals( "id" ) ){
          //. Ignore Disabled
          defID = true;
        }else if( args[i].substring( 1 ).equals( "od" ) ){
          //. Only Domino Mail
          defONLYDOMMAIL = true;
        }else if( args[i].substring( 1 ).equals( "os" ) ){
          //. Only SMTP Mail
          defONLYSMTPMAIL = true;
        }else if( args[i].substring( 1 ).equals( "ms" ) && i + 1 < args.length ){
          //. MAX SERVERS
          defMS = Integer.parseInt( args[++i] );
        }else if( args[i].substring( 1 ).equals( "sw" ) && i + 1 < args.length ){
          //. SCREEN Width
          scn_w = Integer.parseInt( args[++i] );
        }else if( args[i].substring( 1 ).equals( "sh" ) && i + 1 < args.length ){
          //. SCREEN Height
          scn_h = Integer.parseInt( args[++i] );
        }else if( args[i].substring( 1 ).equals( "df" ) && i + 1 < args.length ){
          //. Domain Filter
          defDOMFILT.add( args[++i] );
        }else if( args[i].substring( 1 ).equals( "h" ) ){
          //. HELP
          System.out.println( "Usage: >java dtopo [ -s SERVER -b BASEDIR -n NABFILENAME -c -id -od -os -ms MAXSERVERS]" );
          System.out.println( "    -s SERVER : names.nsf があるサーバーを指定" );
          System.out.println( "       （デフォルトでは \"\" = ローカル）" );
          System.out.println( "    -b BASEDIR : names.nsf があるディスク上のディレクトリを指定" );
          System.out.println( "       （デフォルトでは \"\"）" );
          System.out.println( "    -n NABFILENAME : names.nsf のパスをノーツデータディレクトリから相対指定" );
          System.out.println( "       （デフォルトでは \"names.nsf\"）" );
          System.out.println( "    -sic ICONFILENAME : サーバーを画像ファイルでアイコン表示する（デフォルトでは \"\" 、つまりアイコン表示しない）" );
          System.out.println( "    -cic ICONFILENAME : クラスタグループを画像ファイルでアイコン表示する（デフォルトでは \"\" 、つまりアイコン表示しない）" );
          System.out.println( "    -nic ICONFILENAME : ネットワークを画像ファイルでアイコン表示する（デフォルトでは \"\" 、つまりアイコン表示しない）" );
          System.out.println( "    -c : サーバー名を CANONICAL で表記（デフォルトでは ABBREVIATE 表記）" );
          System.out.println( "    -id : DISABLED なルーティングは無視（デフォルトでは区別されて表記する）" );
          System.out.println( "    -od : メールルーティングはドミノのみ（デフォルトでは false ）" );
          System.out.println( "    -os : メールルーティングは SMTP のみ（デフォルトでは false ）" );
          System.out.println( "    -df DOMAINNAME : 対象のドメインを特定する" );
          System.out.println( "    -ms MAXSERVERS : サーバー数の上限を変更（デフォルトでは " + "30" + " ）" );
          System.out.println( "    -sw SCREEN_WIDTH : スクリーンの横幅（デフォルトでは " + "600" + " ）" );
          System.out.println( "    -sh SCREEN_HEIGHT : スクリーンの高さ（デフォルトでは " + "400" + " ）" );
          System.exit( 0 );
        }
      }
    }

    try{
      cnvFrame mf = new cnvFrame( defSERVER, defBASE, defNAB, defSICON, defCICON, defNICON, defCN, defID, defONLYDOMMAIL, defONLYSMTPMAIL, defDOMFILT, defMS, scn_w, scn_h, 0 );
      cnvFrame rf = new cnvFrame( defSERVER, defBASE, defNAB, defSICON, defCICON, defNICON, defCN, defID, defONLYDOMMAIL, defONLYSMTPMAIL, defDOMFILT, defMS, scn_w, scn_h, 1 );

      mf.setTitle( "NOTES/DOMINO MAIL TOPOLOGY" );
      mf.setBackground( Color.white );
      mf.addWindowListener( new appCloser() );
      mf.pack();
      mf.setSize( scn_w, scn_h );
      mf.show();

      rf.setTitle( "NOTES/DOMINO REPLICATION TOPOLOGY" );
      rf.setBackground( Color.white );
      rf.addWindowListener( new appCloser() );
      rf.pack();
      rf.setSize( scn_w, scn_h );
      rf.setBounds( 20, 20, scn_w, scn_h );
      rf.show();

    }catch( Throwable t ){
      System.out.println( "( dtopo.main() ) uncaught exception: " + t );
      t.printStackTrace();
    }
  }

  protected static final class appCloser extends WindowAdapter{
    public void windowClosing( WindowEvent e ){
      System.exit( 0 );
    }
  }
}

class cnvFrame extends Frame{
  cnvFrame( String server, String base, String nab, String sicon, String cicon, String nicon, boolean cn, boolean id, boolean od, boolean os, Vector df, int ms, int sw, int sh, int type ){
    super( "dtopo" );

    iCanvas cnv = new iCanvas( server, base, nab, sicon, cicon, nicon, cn, id, od, os, df, ms, sw, sh, type );
    add( "Center", cnv );
  }
}

class iCanvas extends Canvas{
  Vector lst1 = new Vector();
  Vector lst2 = new Vector();
  Vector tlst = new Vector();
  Vector srvlst = new Vector();
  Vector pc_cl = new Vector();
  int rlst[];
  int xlst[];
  int ylst[];
  String defSERVER = "";
  String defBASE = "";
  String defNAB;
  String defSICON = "";
  String defCICON = "";
  String defNICON = "";
  boolean defCN;
  boolean defID;
  boolean defONLYDOMMAIL;
  boolean defONLYSMTPMAIL;
  int defMS;
  int defTYPE;
  Vector defDOMFILT = new Vector();
  int scn_w, scn_h;
  double rs;
  int flag = -1;
  int activeIndex;
  Image sicon = null;
  Image cicon = null;
  Image nicon = null;
  boolean iconed = false;

  iCanvas( String server, String base, String nab, String siconfilename, String ciconfilename, String niconfilename, boolean cn, boolean id, boolean od, boolean os, Vector df, int ms, int sw, int sh, int type){
    defSERVER = server;
    defBASE = base;
    defNAB = nab;
    defSICON = siconfilename;
    defCICON = ciconfilename;
    defNICON = niconfilename;
    defCN = cn;
    defID = id;
    defONLYDOMMAIL = od;
    defONLYSMTPMAIL = os;
    defMS = ms;
    scn_w = sw; scn_h = sh;
    defTYPE = type;  //. 0:メール　1:レプリカ
    for( int i = 0; i < df.size(); i ++ ){
      defDOMFILT.add( ( String )df.get( i ) );
    }

    rlst = new int[defMS];
    xlst = new int[defMS];
    ylst = new int[defMS];

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

    GetRepInfo nt = new GetRepInfo();
    nt.start();
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

      drawArrow( g, ct[c], x1 + c + 2, y1 + c+ 2, x2 + c + 2, y2 + c + 2 );
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
//.    g.drawString( "LPS @ IBM Software Group Japan, all rights reserved.", sw - 300, sh - 5 );
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


  public class GetRepInfo extends NotesThread{
    public void runNotes(){
      int n;

      try{
        n = searchNAB( defSERVER, defBASE, defNAB, defSICON, defCICON, defNICON, defCN, defID, defONLYDOMMAIL, defONLYSMTPMAIL, defDOMFILT, defTYPE );

        if( n > 0 ){
          repaint();
        }
      }catch( Exception e ){
        e.printStackTrace();
      }
    }

    int searchNAB( String server, String base, String nab, String sicon, String cicon, String nicon, boolean cn, boolean id, boolean od, boolean os, Vector df, int type ){
      int cnt = 0;
      int i, m, n, k;
      int x1, y1;
      double t;
      Vector tasksV;
      boolean sbl, dbl;
      String dfitem;


      try{
        Session s = Session.newInstance();
        String dbpath;
        String sserv, dserv, reptype, enabled, tasks;
        Vector sservs;
        String sdom, ddom;

        dbpath = nab;
        if( base.length() > 0 ){
          dbpath = base + "\\" + dbpath;
        }
        Database db = s.getDatabase( server, dbpath );
        if( !db.isOpen() ){
        	db.open();
        }

        //. 接続文書を検索する
        DocumentCollection docs = db.search( "Type = \"Connection\"" );
        for( int dcnt = 0; dcnt < docs.getCount(); dcnt ++ ){
            Document doc = docs.getNthDocument( dcnt + 1 );

//          sserv = doc.getItemValueString( "Source" );
//          dserv = doc.getItemValueString( "Destination" );
          if( defCN ){
            sserv = s.createName( doc.getItemValueString( "Source" ) ).getCommon();
            dserv = s.createName( doc.getItemValueString( "Destination" ) ).getCommon();
          }else{
            sserv = s.createName( doc.getItemValueString( "Source" ) ).getAbbreviated();
            dserv = s.createName( doc.getItemValueString( "Destination" ) ).getAbbreviated();
          }

          sdom = s.createName( doc.getItemValueString( "Source" ) ).getOrganization();
          ddom = s.createName( doc.getItemValueString( "Destination" ) ).getOrganization();

          m = df.size();
          sbl = true; dbl = true;
          for( i = 0; i < m && ( sbl || dbl ); i ++ ){
            dfitem = ( String )df.get( i );

            if( ( sbl ) && ( sdom.toUpperCase().indexOf( dfitem.toUpperCase() ) > -1 ) ){
              sbl = false;
            }
            if( ( dbl ) && ( ddom.toUpperCase().indexOf( dfitem.toUpperCase() ) > -1 ) ){
              dbl = false;
            }
          }

          if( m > 0 && ( sbl || dbl ) ){
            //. フィルタが定義されているのに、フィルタにかからなかった
            continue;
          }

          //. type  0:メール　1:レプリカ
          if( type == 0 ){
            reptype = doc.getItemValueString( "RouterType" );
            if( reptype == null ){
              reptype = "" + doc.getItemValueInteger( "RouterType" );
            }
            enabled = doc.getItemValueString( "Enabled" );
            if( enabled == null ){
              enabled = "" + doc.getItemValueInteger( "Enabled" );
            }
          }else if( type == 1 ){
            reptype = doc.getItemValueString( "RepType" );
            if( reptype == null ){
              reptype = "" + doc.getItemValueInteger( "RepType" );
            }
//            enabled = doc.getItemValueString( "RepTask" );
//            if( enabled == null ){
//              enabled = "" + doc.getItemValueInteger( "RepTask" );
//            }
            enabled = doc.getItemValueString( "Enabled" );
            if( enabled == null ){
              enabled = "" + doc.getItemValueInteger( "Enabled" );
            }
          }else{
            reptype = "";
            enabled = "";
            continue;
          }

          if( enabled.equals( "0" ) ){
            continue;
          }

          tasksV = doc.getItemValue( "Tasks" );
          n = tasksV.size();
          for( i = 0; i < n; i ++ ){
            tasks = ( String )tasksV.get( i );

//. System.out.println( "" + sserv + " -> " + dserv + ", RepType = " + reptype + ", Enabled = " + enabled + ", type = " + type + ", Tasks = " + tasks );
            if( ( type == 1 ) && ( tasks.toUpperCase().indexOf( "REPLICATION" ) > -1 ) && ( enabled == null || enabled.equals( "0" ) ) ){
              if( id ){
                continue;
              }
            }

            if( type == 0 ){
              if( tasks.toUpperCase().indexOf( "MAIL ROUTING" ) == -1 && tasks.toUpperCase().indexOf( "SMTP MAIL" ) == -1 ){
                continue;
              }else if( od && tasks.toUpperCase().indexOf( "SMTP MAIL" ) > -1 ){
                continue;
              }else if( os && tasks.toUpperCase().indexOf( "SMTP MAIL" ) == -1 ){
                continue;
              }

              if( reptype != null && tasks.toUpperCase().indexOf( "SMTP MAIL" ) > -1 ){
               reptype = "4";
              }
            }
            if( type == 1 && tasks.toUpperCase().indexOf( "REPLICATION" ) == -1 ){
              continue;
            }

            if( reptype != null ){
              if( addList( sserv, dserv, reptype ) ){
                cnt ++;
              }

              if( !isInServerList( sserv ) ){
                srvlst.add( sserv );

                if( sdom.length() > 0 ){
                  pc_cl.add( "1" );
                }else{
                  pc_cl.add( "0" );
                }
              }

              if( !isInServerList( dserv ) ){
               srvlst.add( dserv );

                if( ddom.length() > 0 ){
                  pc_cl.add( "1" );
                }else{
                  pc_cl.add( "0" );
                }
              }
            }
          }
        }

        //. ネットワーク文書を検索する
        if( type == 0 ){
          docs = db.search( "Type = \"Server\" & Form = \"Server\"" );
          for( int dcnt = 0; dcnt < docs.getCount(); dcnt ++ ){
              Document doc = docs.getNthDocument( dcnt + 1 );

              dserv = s.createName( doc.getItemValueString( "ServerName" ) ).getAbbreviated();
              ddom = s.createName( doc.getItemValueString( "ServerName" ) ).getOrganization();
              sservs = doc.getItemValue( "Network" );
              n = sservs.size();
              for( k = 0; k < n; k ++ ){
                sserv = "(" + ( String )sservs.get( k ) + ")";

                //. K.Kimura
                m = df.size();
                dbl = true;
                for( i = 0; i < m && dbl ; i ++ ){
                    dfitem = ( String )df.get( i );

                    if( ( dbl ) && ( ddom.toUpperCase().indexOf( dfitem.toUpperCase() ) > -1 ) ){
                      dbl = false;
                    }
                }

                if( m > 0 && dbl ){
                    //. フィルタが定義されているのに、フィルタにかからなかった
                    continue;
                }

                if( addList( sserv, dserv, "5" ) ){
                    cnt ++;
                }

                if( !isInServerList( sserv ) ){
                    srvlst.add( sserv );
                    pc_cl.add( "2" );
                }

                if( !isInServerList( dserv ) ){
                    srvlst.add( dserv );

                    if( ddom.length() > 0 ){
                        pc_cl.add( "1" );
                    }else{
                        pc_cl.add( "0" );
                    }
                }
              }
            }
        }

      }catch( NotesException e ){
        //. ＤＢが存在していない可能性もある・・・
        System.out.println( "NotesException: " + e );
        e.printStackTrace();

      }catch( Exception e ){
        System.out.println( "Exception: " + e );
        e.printStackTrace();
      }

      m = srvlst.size();
      rs = Math.min( scn_w, scn_h ) * 0.4;
      if( m > 0 ){
        t = ( 2 * Math.PI ) / m;
        for( i = 0; i < m; i ++ ){
          x1 = ( int )( scn_w / 2 + ( rs * Math.cos( i * t ) ) );
          y1 = ( int )( scn_h / 2 - ( rs * Math.sin( i * t ) ) );

          xlst[i] = x1;
          ylst[i] = y1;
        }
      }

      flag = 0;


      try{
        PrintWriter pw = new PrintWriter( new BufferedWriter( new FileWriter( "dtopo" + type + ".dat" ) ) );

        pw.println( "sw: " + scn_w );
        pw.println( "sh: " + scn_h );
        pw.println( "sic: " + sicon );
        pw.println( "cic: " + cicon );
        pw.println( "nic: " + nicon );
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


      return cnt;
    }

    boolean addList( String src, String dst, String rep ){
      int vsize = lst1.size();
      int i;
      boolean b = true;
      String line1, line2;
      int iRep, oldRep;

      for( i = 0; i < vsize && b; i ++ ){
        line1 = ( String )lst1.get( i );
        line2 = ( String )lst2.get( i );
        oldRep = rlst[i];
        iRep = Integer.parseInt( rep );

        if( line1.toUpperCase().equals( src.toUpperCase() ) && line2.toUpperCase().equals( dst.toUpperCase() ) && iRep == oldRep ){
          b = false;
        }
      }

      if( b ){
        if( vsize >= defMS ){
          System.out.println( "サーバーの数が " + defMS + " を超えているため、処理できません。" );
          return false;
        }

        lst1.add( src );
        lst2.add( dst );
        rlst[vsize] = Integer.parseInt( rep );
      }

      return b;
    }


    boolean isInServerList( String srv ){
      int vsize = srvlst.size();
      int i;
      boolean b = false;
      String line;

      for( i = 0; i < vsize && !b; i ++ ){
        line = ( String )srvlst.get( i );
        if( srv.toUpperCase().equals( line.toUpperCase() ) ){
          b = true;
        }
      }

      return b;
    }
  }
}
