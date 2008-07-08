/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.controller.db.tests;

import java.io.ByteArrayOutputStream;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.sourceforge.toscanaj.controller.db.DatabaseConnection;
import net.sourceforge.toscanaj.controller.db.DatabaseException;
import net.sourceforge.toscanaj.controller.db.DumpSqlScript;
import net.sourceforge.toscanaj.model.database.DatabaseInfo;

import org.tockit.events.EventBroker;

public class DumpSqlScriptTest extends TestCase {

    public static Test suite() {
        return new TestSuite(DumpSqlScriptTest.class);
    }

    public DumpSqlScriptTest(final String testName) {
        super(testName);
    }

    public void testDumpSQLScript() {

        try {
            final DatabaseInfo info = DatabaseInfo.getEmbeddedDatabaseInfo();

            final DatabaseConnection connection = new DatabaseConnection(
                    new EventBroker());

            connection.connect(info);
            connection.executeSQLAsString(SQL_SCRIPT);

            final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            DumpSqlScript.dumpSqlScript(connection, outputStream);

            assertEquals(SQL_SCRIPT, outputStream.toString());

            connection.disconnect();
        } catch (final DatabaseException e) {
            fail("DB Exception: " + e.getMessage() + " - "
                    + e.getCause().getMessage());
        }

    }

    public static final String lineBreak = System.getProperty("line.separator");

    public static final String SQL_SCRIPT = "CREATE TABLE PCTEST ("
            + lineBreak
            + "  PCNAME varchar,"
            + lineBreak
            + "  PRICE integer,"
            + lineBreak
            + "  HARDDISK integer,"
            + lineBreak
            + "  CPU float,"
            + lineBreak
            + "  RAM float,"
            + lineBreak
            + "  VIDEO bigint,"
            + lineBreak
            + "  DISK float,"
            + lineBreak
            + "  DOSMARK float,"
            + lineBreak
            + "  GRAPHICS float,"
            + lineBreak
            + "  DISKMARK float,"
            + lineBreak
            + "  DEALER tinyint,"
            + lineBreak
            + "  DIRECTSALES tinyint,"
            + lineBreak
            + "  TYPECASE varchar,"
            + lineBreak
            + "  TYPEBUS varchar,"
            + lineBreak
            + "  POWERSUPPLY integer,"
            + lineBreak
            + "  POWERCONNECTORS integer,"
            + lineBreak
            + "  UPGRADABILITY varchar,"
            + lineBreak
            + "  NUMERICPUSOCKET varchar,"
            + lineBreak
            + "  CACHEARCHITECTURE varchar,"
            + lineBreak
            + "  CACHEWRITE varchar,"
            + lineBreak
            + "  FREEDRIVESLOTS integer,"
            + lineBreak
            + "  INTERNALDRIVESLOTS integer,"
            + lineBreak
            + "  DISKDRIVES varchar,"
            + lineBreak
            + "  HARDDISKBAYS varchar,"
            + lineBreak
            + "  HARDDISKCONTROLLER varchar,"
            + lineBreak
            + "  SLOTS8BIT integer,"
            + lineBreak
            + "  SLOTS16BIT integer,"
            + lineBreak
            + "  SLOTSEISA integer,"
            + lineBreak
            + "  SLOTSMCA integer,"
            + lineBreak
            + "  SLOTSOTHER integer,"
            + lineBreak
            + "  SLOTSLOCALBUS integer,"
            + lineBreak
            + "  PORTS varchar,"
            + lineBreak
            + "  NETWORK varchar,"
            + lineBreak
            + "  GRAPHICCARD varchar,"
            + lineBreak
            + "  DOSWIN varchar,"
            + lineBreak
            + "  SOFTWARE varchar,"
            + lineBreak
            + "  WARRANTY varchar,"
            + lineBreak
            + "  SERVICE varchar,"
            + lineBreak
            + "  LARGEDRIVES integer,"
            + lineBreak
            + "  SMALLDRIVES integer,"
            + lineBreak
            + "  LARGEINTERNALDRIVES integer,"
            + lineBreak
            + "  SMALLINTERNALDRIVES integer,"
            + lineBreak
            + "  DOS varchar,"
            + lineBreak
            + "  WIN varchar"
            + lineBreak
            + ");"
            + lineBreak
            + ""
            + lineBreak
            + "INSERT INTO PCTEST VALUES ('ALR Flyer 32DT 4DX2/66',3962,200,16767.0,7947.0,4632,28.83,33.88,5.82,40.51,1,1,'Slimline','ISA',145,4,'Processor card','nein','Direct-mapped','wb',11,10,'1,2MB, 1,44MB',20,'Motherboard',0,3,0,0,3,0,'2,1,0','none','ISA','jj','nein','1 Jahr','9.95 $',1,1,1,0,'ja','ja');"
            + lineBreak
            + "INSERT INTO PCTEST VALUES ('American Mitac TL4466',3499,380,15799.0,3685.0,1285,160.2,60.01,6.25,50.36,1,1,'Tower','EISA',302,5,'Standard socket','ja','Direct-mapped','wb',70,0,'1,44MB',16,'EISA',0,2,6,0,0,0,'Keine','none','EISA','jj','nein','1 Jahr','99 $',7,0,0,0,'ja','ja');"
            + lineBreak
            + "INSERT INTO PCTEST VALUES ('American Super Computer 486X2/e66',3569,240,12371.0,8658.0,2903,32.2,35.05,7.44,44.13,1,1,'Tower','EISA',230,6,'Processor card','ja','Four-way set-associative','wt',41,40,'1,2MB, 1,44MB',34,'EISA',0,1,6,0,1,0,'Keine','none','EISA','jj','nein','2 Jahre','268 $',4,1,4,0,'ja','ja');"
            + lineBreak
            + "INSERT INTO PCTEST VALUES ('Arche Legacy 486/66DX2',4498,212,15843.0,5216.0,5256,28.68,31.49,44.47,43.64,1,1,'Small-footprint','ISA',200,4,'Standard socket','ja','Direct-mapped','wb',20,10,'1,2MB, 1,44MB',22,'ISA',1,7,0,0,0,0,'Keine','none','ISA','jj','ja','2 Jahre','inklusive',2,0,1,0,'ja','ja');"
            + lineBreak
            + "INSERT INTO PCTEST VALUES ('Ares 486-66DX2 VL-Bus',4350,240,16446.0,6275.0,4350,25.41,29.87,10.24,45.26,1,0,'Desktop','ISA',250,5,'Standard socket','nein','Direct-mapped','wt',21,20,'Dual',50,'Motherboard',0,8,0,0,0,2,'1,2,0','none','ISA','jj','ja','2 Jahre','inklusive',2,1,2,0,'ja','ja');"
            + lineBreak
            + "INSERT INTO PCTEST VALUES ('Ariel 486DX2-66VLB',4295,520,16441.0,6275.0,7013,24.77,29.37,15.62,46.31,0,1,'Tower','ISA',300,4,'Standard socket','nein','Direct-mapped','wt',50,22,'1,2MB, 1,44MB',59,'ISA',0,8,0,0,0,2,'2,1,0','none','VESA Local Bus','jj','ja','1 Jahr','50 $',5,0,2,2,'ja','ja');"
            + lineBreak
            + "INSERT INTO PCTEST VALUES ('AST Bravo 4/66d',4205,340,16213.0,6178.0,3759,32.77,35.54,4.45,41.59,1,0,'Slimline','ISA',145,4,'Standard socket','nein','Direct-mapped','wb',20,20,'1,2MB, 1,44MB',41,'Motherboard',0,4,0,0,0,0,'2,1,1','Ethernet','Motherboard','jj','nein','1 Jahr','inklusive',2,0,2,0,'ja','ja');"
            + lineBreak
            + "INSERT INTO PCTEST VALUES ('ATronics ATI-486-66',3495,340,15582.0,4453.0,1732,117.06,59.99,6.82,28.22,1,1,'Small-footprint','ISA',200,4,'Standard socket','ja','Direct-mapped','wb',32,0,'1,2MB, 1,44MB',27,'ISA',1,7,0,0,1,0,'Keine','none','ISA','jj','nein','1 Jahr','99 $',3,2,0,0,'ja','ja');"
            + lineBreak
            + "INSERT INTO PCTEST VALUES ('Austin 466DX2 WinStation',2990,220,16439.0,6249.0,7007,24.05,28.72,22.99,45.77,1,1,'Desktop','ISA',200,5,'Standard socket','nein','Direct-mapped','wt',30,2,'1,2MB, 1,44MB',4,'Motherboard',0,8,0,0,0,2,'2,1,0','none','VESA Local Bus','jj','ja','1 Jahr','inklusive',3,0,0,2,'ja','ja');"
            + lineBreak
            + "INSERT INTO PCTEST VALUES ('Bi-Link Desktop i486DX2/66',2665,210,14556.0,4463.0,1789,29.34,30.44,8.82,40.6,0,1,'Tower','ISA',300,6,'Standard socket','nein','Direct-mapped','wb',50,21,'1,2MB, 1,44MB',42,'ISA',1,7,0,0,0,1,'Keine','none','Proprietary Local Bus','jj','nein','1 Jahr','',5,0,2,1,'ja','ja');"
            + lineBreak
            + "INSERT INTO PCTEST VALUES ('BLK 486DX2/66',2995,213,17206.0,8301.0,2654,29.34,34.49,1.76,39.47,0,1,'Minitower','ISA',250,5,'Standard socket','ja','Two-way set-associative','wb',22,1,'1,2MB, 1,44MB',9,'Motherboard',0,8,0,0,0,0,'1,2,0','none','ISA','jj','nein','1 Jahr','inklusive',2,2,0,1,'ja','ja');"
            + lineBreak
            + "INSERT INTO PCTEST VALUES ('Blue Star 466D2U',2949,340,14514.0,4463.0,3701,36.87,35.3,7.98,41.32,0,1,'Desktop','ISA',250,4,'Standard socket','ja','Direct-mapped','wb',21,20,'Dual',37,'ISA',0,8,0,0,1,0,'Keine','none','ISA','jj','ja','1 Jahr','59 $',2,1,2,0,'ja','ja');"
            + lineBreak
            + "INSERT INTO PCTEST VALUES ('BOSS 466d',4495,245,17197.0,8301.0,3183,38.1,41.53,88.83,47.79,1,0,'Minitower','ISA',300,6,'Standard socket','ja','Four-way set-associative','wb',32,10,'1,2MB, 1,44MB',36,'ISA',1,7,0,0,0,0,'1,2,0','none','ISA','jj','nein','2 Jahre','inklusive',3,2,1,0,'ja','ja');"
            + lineBreak
            + "INSERT INTO PCTEST VALUES ('Broadax 486DX2-66',2260,212,14292.0,3920.0,2644,28.0,28.8,2.41,34.78,1,1,'Desktop','ISA',200,5,'Standard socket','nein','Direct-mapped','wb',30,2,'1,2MB, 1,44MB',5,'ISA',2,6,0,0,0,0,'Keine','none','ISA','jj','nein','1 Jahr','80 $',3,0,0,2,'ja','ja');"
            + lineBreak
            + "INSERT INTO PCTEST VALUES ('C� Saber 486/e DX2-66',4145,358,12402.0,8658.0,4998,40.14,40.66,18.09,43.4,1,0,'Tower','EISA',250,6,'Processor card','nein','Direct-mapped','wt',41,40,'1,2MB, 1,44MB',12,'EISA',0,1,6,0,0,0,'Keine','none','ISA','jj','nein','1 Jahr','75 $',4,1,4,0,'ja','ja');"
            + lineBreak
            + "INSERT INTO PCTEST VALUES ('CAF Gold 6D2',2459,213,16869.0,5537.0,2627,25.14,29.11,4.61,42.14,1,0,'Desktop','ISA',200,5,'Standard socket','ja','Direct-mapped','wb',32,2,'1,2MB, 1,44MB',2,'ISA',0,8,0,0,0,0,'Keine','none','Motherboard','jj','nein','1 Jahr','nein',3,2,0,2,'ja','ja');"
            + lineBreak
            + "INSERT INTO PCTEST VALUES ('Clover 486 Quick-I Series',3459,245,16477.0,6124.0,1885,37.76,38.78,6.58,38.27,0,1,'Small-footprint','ISA',200,4,'ZIF socket','ja','Direct-mapped','wb',30,4,'1,2MB, 1,44MB',44,'ISA',0,8,0,0,0,0,'Keine','none','ISA','jj','nein','1 Jahr','inklusive',3,0,0,4,'ja','ja');"
            + lineBreak
            + "INSERT INTO PCTEST VALUES ('Comex 486DX2/66',2750,202,16526.0,6254.0,2766,24.74,29.21,7.63,40.45,1,0,'Desktop','ISA',250,5,'Standard socket','ja','Direct-mapped','wt',21,11,'1,2MB, 1,44MB',1,'ISA',0,8,1,0,0,0,'Keine','none','ISA','jj','nein','2 Jahre','inklusive',2,1,1,1,'ja','ja');"
            + lineBreak
            + "INSERT INTO PCTEST VALUES ('Compaq Deskpro 66M',5192,211,17326.0,8629.0,6159,29.41,34.93,12.34,35.4,1,0,'Desktop','EISA',240,4,'Processor card','ja','Four-way set-associative','wb',30,1,'1,44MB',28,'Motherboard',0,0,5,0,2,0,'1,2,1','none','EISA','jj','ja','1 Jahr','inklusive',3,0,0,1,'ja','ja');"
            + lineBreak
            + "INSERT INTO PCTEST VALUES ('CompuAdd 466E',4213,212,15769.0,3802.0,4865,37.48,34.79,5.65,46.31,1,1,'Desktop','EISA',200,5,'Standard socket','ja','Direct-mapped','wb',21,4,'1,2MB, 1,44MB',18,'EISA',0,0,8,0,0,0,'Keine','none','EISA','jj','nein','1 Jahr','inklusive',2,1,0,4,'ja','ja');"
            + lineBreak
            + "INSERT INTO PCTEST VALUES ('CompuAdd Express 466DX Scalable',2675,200,15125.0,4708.0,9955,27.15,29.66,5.55,27.75,0,1,'Slimline','ISA',150,5,'Standard socket','nein','Direct-mapped','wb',11,1,'1,2MB, 1,44MB',15,'Motherboard',2,3,0,0,0,0,'1,2,0','none','Motherboard','jj','nein','1 Jahr','inklusive',1,1,0,1,'ja','ja');"
            + lineBreak
            + "INSERT INTO PCTEST VALUES ('Comtrade 486 EISA Dream Machine',3095,212,17320.0,9510.0,3466,76.99,65.18,4.21,29.06,1,1,'Tower','EISA',230,5,'Standard socket','nein','Direct-mapped','wb',33,2,'1,2MB, 1,44MB',49,'EISA',0,0,7,0,0,1,'Keine','none','ISA','jj','nein','2 Jahre, lebenslang f�r Arbeit','inklusive',3,3,0,2,'ja','ja');"
            + lineBreak
            + "INSERT INTO PCTEST VALUES ('Dell 466DE/2',4069,230,16268.0,10155.0,2970,38.12,42.22,7.44,45.45,1,1,'Small-footprint','EISA',224,4,'Standard socket','nein','Direct-mapped','wt',30,1,'1,2MB, 1,44MB',24,'Motherboard',0,0,6,0,0,0,'2,1,1','none','ISA','jj','nein','1 Jahr','inklusive',3,0,0,1,'ja','ja');"
            + lineBreak
            + "INSERT INTO PCTEST VALUES ('DFI 486-66DX2',3982,202,14427.0,3865.0,4395,108.38,54.97,9.42,28.01,1,0,'Tower','ISA',250,5,'Standard socket','nein','Direct-mapped','wb',40,20,'1,2MB, 1,44MB',52,'ISA',0,7,0,0,1,2,'Keine','none','UBSA Local Bus','jj','nein','1 Jahr','inklusive',4,0,2,0,'ja','ja');"
            + lineBreak
            + "INSERT INTO PCTEST VALUES ('Diamond DX2-66',2995,245,17088.0,6917.0,2811,33.48,36.93,7.34,53.15,0,1,'Tower','EISA',200,4,'Standard socket','ja','Direct-mapped','wb',32,2,'1,2MB, 1,44MB',23,'EISA',0,0,8,0,0,0,'Keine','none','ISA','jj','nein','15 Monate, 2 Jahre f�r Arbeit','75 $',3,2,0,2,'ja','ja');"
            + lineBreak
            + "INSERT INTO PCTEST VALUES ('Digital DECpc 466d2 LP',3124,240,16196.0,4659.0,2106,37.35,36.39,9.02,39.44,1,1,'Slimline','ISA',146,4,'Processor card','nein','Direct-mapped','wb',2,2,'1,44MB',14,'Motherboard',0,3,0,0,0,0,'1,2,1','none','Proprietary Local Bus','jj','nein','1 Jahr','inklusive',0,2,0,2,'ja','ja');"
            + lineBreak
            + "INSERT INTO PCTEST VALUES ('Edge 466 Magnum',3499,213,17139.0,9582.0,2414,208.55,96.58,10.01,141.57,0,1,'Slimline','EISA',200,5,'Standard socket','ja','Direct-mapped','wb',30,2,'Dual',60,'EISA',0,7,0,0,0,1,'Keine','none','Proprietary Local Bus','jn','nein','1 Jahr','inklusive',3,0,0,2,'ja','nein');"
            + lineBreak
            + "INSERT INTO PCTEST VALUES ('EPS ISA 486 DX2/66',2945,340,14181.0,6578.0,2610,36.78,37.76,6.85,42.57,0,1,'Small-footprint','ISA',200,5,'Standard socket','ja','Direct-mapped','wb',21,20,'1,2MB, 1,44MB',23,'ISA',2,6,0,0,0,0,'Keine','none','ISA','jj','nein','3 Jahre','inklusive',2,1,2,0,'ja','ja');"
            + lineBreak
            + "INSERT INTO PCTEST VALUES ('Everex Tempo M Series 486 DX2/66',4563,244,16024.0,7378.0,4473,30.94,35.06,2.92,50.72,1,0,'Desktop','ISA',200,4,'Processor card','nein','Direct-mapped','wb',30,20,'1,2MB, 1,44MB',3,'Motherboard',1,6,0,0,1,0,'1,2,0','none','Motherboard','jj','nein','1 Jahr','inklusive',3,0,2,0,'ja','ja');"
            + lineBreak
            + "INSERT INTO PCTEST VALUES ('Expo 486 dX2/66',2799,245,17244.0,7273.0,2805,39.71,41.8,7.62,51.85,0,1,'Tower','ISA',300,5,'Standard socket','ja','Four-way set-associative','wb',60,20,'1,2MB, 1,44MB',64,'ISA',2,8,0,0,0,0,'Keine','none','ISA','jj','ja','1 Jahr','inklusive',6,0,2,0,'ja','ja');"
            + lineBreak
            + "INSERT INTO PCTEST VALUES ('FCS 486-66',3999,330,16900.0,5071.0,1628,30.2,32.43,5.49,44.26,1,1,'Tower','ISA',300,6,'Standard socket','ja','Direct-mapped','wb',30,20,'1,2MB, 1,44MB',53,'ISA',0,7,0,0,0,0,'Keine','none','Motherboard','jj','nein','1 Jahr, 2 Jahre f�r Arbeit','keine Angabe',3,0,2,0,'ja','ja');"
            + lineBreak
            + "INSERT INTO PCTEST VALUES ('FutureTech System 462E',4529,425,16653.0,6070.0,2935,102.27,65.53,7.64,60.12,1,1,'Tower','EISA',300,6,'Standard socket','nein','Direct-mapped','wb',60,30,'1,2MB, 1,44MB',47,'EISA',0,0,8,0,0,0,'Keine','none','ISA','jj','nein','15 Monate','79 $',6,0,3,0,'ja','ja');"
            + lineBreak
            + "INSERT INTO PCTEST VALUES ('Gateway 2000 4DX2-66V',2995,340,16400.0,6239.0,6992,30.85,34.32,21.18,53.84,0,1,'Desktop','ISA',200,5,'Standard socket','nein','Two-way set-associative','wt',32,20,'1,2MB, 1,44MB',23,'Motherboard',0,6,0,0,0,2,'2,1,0','none','Motherboard','jj','ja','1 Jahr','inklusive',3,2,2,0,'ja','ja');"
            + lineBreak
            + "INSERT INTO PCTEST VALUES ('GCH EasyData 486DX-2/66',3860,340,13578.0,7084.0,1037,36.23,36.96,5.8,46.05,1,0,'Small-footprint','ISA',220,4,'Standard socket','nein','Direct-mapped','wb',31,1,'1,2MB, 1,44MB',23,'EISA',1,6,0,0,0,0,'Keine','none','EISA','jj','ja','1 Jahr','99 $',3,1,0,1,'ja','ja');"
            + lineBreak
            + "INSERT INTO PCTEST VALUES ('Gecco 466E',3225,240,17199.0,8238.0,3390,32.25,36.97,2.04,44.0,0,1,'Tower','EISA',250,5,'Standard socket','ja','Direct-mapped','wb',40,13,'1,2MB, 1,44MB',65,'EISA',0,2,6,0,0,0,'Keine','none','EISA','jj','nein','2 Jahre','inklusive',4,0,1,3,'ja','ja');"
            + lineBreak;
}
