CREATE TABLE PCTEST(
  PCNAME varchar,
  PRICE integer,
  HARDDISK integer,
  CPU float,
  RAM float,
  VIDEO bigint,
  DISK float,
  DOSMARK float,
  GRAPHICS float,
  DISKMARK float,
  DEALER tinyint,
  DIRECTSALES tinyint,
  TYPECASE varchar,
  TYPEBUS varchar,
  POWERSUPPLY integer,
  POWERCONNECTORS integer,
  UPGRADABILITY varchar,
  NUMERICPUSOCKET varchar,
  CACHEARCHITECTURE varchar,
  CACHEWRITE varchar,
  FREEDRIVESLOTS integer,
  INTERNALDRIVESLOTS integer,
  DISKDRIVES varchar,
  HARDDISKBAYS varchar,
  HARDDISKCONTROLLER varchar,
  SLOTS8BIT integer,
  SLOTS16BIT integer,
  SLOTSEISA integer,
  SLOTSMCA integer,
  SLOTSOTHER integer,
  SLOTSLOCALBUS integer,
  PORTS varchar,
  NETWORK varchar,
  GRAPHICCARD varchar,
  DOSWIN varchar,
  SOFTWARE varchar,
  WARRANTY varchar,
  SERVICE varchar,
  LARGEDRIVES integer,
  SMALLDRIVES integer,
  LARGEINTERNALDRIVES integer,
  SMALLINTERNALDRIVES integer,
  DOS varchar,
  WIN varchar
);

INSERT INTO PCTEST VALUES ('ALR Flyer 32DT 4DX2/66',3962,200,16767.0,7947.0,4632,28.83,33.88,5.82,40.51,1,1,'Slimline','ISA',145,4,'Processor card','nein','Direct-mapped','wb',11,10,'1,2MB, 1,44MB',20,'Motherboard',0,3,0,0,3,0,'2,1,0','none','ISA','jj','nein','1 Jahr','9.95 $',1,1,1,0,'ja','ja');
INSERT INTO PCTEST VALUES ('American Mitac TL4466',3499,380,15799.0,3685.0,1285,160.2,60.01,6.25,50.36,1,1,'Tower','EISA',302,5,'Standard socket','ja','Direct-mapped','wb',70,0,'1,44MB',16,'EISA',0,2,6,0,0,0,'Keine','none','EISA','jj','nein','1 Jahr','99 $',7,0,0,0,'ja','ja');
INSERT INTO PCTEST VALUES ('American Super Computer 486X2/e66',3569,240,12371.0,8658.0,2903,32.2,35.05,7.44,44.13,1,1,'Tower','EISA',230,6,'Processor card','ja','Four-way set-associative','wt',41,40,'1,2MB, 1,44MB',34,'EISA',0,1,6,0,1,0,'Keine','none','EISA','jj','nein','2 Jahre','268 $',4,1,4,0,'ja','ja');
INSERT INTO PCTEST VALUES ('Arche Legacy 486/66DX2',4498,212,15843.0,5216.0,5256,28.68,31.49,44.47,43.64,1,1,'Small-footprint','ISA',200,4,'Standard socket','ja','Direct-mapped','wb',20,10,'1,2MB, 1,44MB',22,'ISA',1,7,0,0,0,0,'Keine','none','ISA','jj','ja','2 Jahre','inklusive',2,0,1,0,'ja','ja');
INSERT INTO PCTEST VALUES ('Ares 486-66DX2 VL-Bus',4350,240,16446.0,6275.0,4350,25.41,29.87,10.24,45.26,1,0,'Desktop','ISA',250,5,'Standard socket','nein','Direct-mapped','wt',21,20,'Dual',50,'Motherboard',0,8,0,0,0,2,'1,2,0','none','ISA','jj','ja','2 Jahre','inklusive',2,1,2,0,'ja','ja');
INSERT INTO PCTEST VALUES ('Ariel 486DX2-66VLB',4295,520,16441.0,6275.0,7013,24.77,29.37,15.62,46.31,0,1,'Tower','ISA',300,4,'Standard socket','nein','Direct-mapped','wt',50,22,'1,2MB, 1,44MB',59,'ISA',0,8,0,0,0,2,'2,1,0','none','VESA Local Bus','jj','ja','1 Jahr','50 $',5,0,2,2,'ja','ja');
INSERT INTO PCTEST VALUES ('AST Bravo 4/66d',4205,340,16213.0,6178.0,3759,32.77,35.54,4.45,41.59,1,0,'Slimline','ISA',145,4,'Standard socket','nein','Direct-mapped','wb',20,20,'1,2MB, 1,44MB',41,'Motherboard',0,4,0,0,0,0,'2,1,1','Ethernet','Motherboard','jj','nein','1 Jahr','inklusive',2,0,2,0,'ja','ja');
INSERT INTO PCTEST VALUES ('ATronics ATI-486-66',3495,340,15582.0,4453.0,1732,117.06,59.99,6.82,28.22,1,1,'Small-footprint','ISA',200,4,'Standard socket','ja','Direct-mapped','wb',32,0,'1,2MB, 1,44MB',27,'ISA',1,7,0,0,1,0,'Keine','none','ISA','jj','nein','1 Jahr','99 $',3,2,0,0,'ja','ja');
INSERT INTO PCTEST VALUES ('Austin 466DX2 WinStation',2990,220,16439.0,6249.0,7007,24.05,28.72,22.99,45.77,1,1,'Desktop','ISA',200,5,'Standard socket','nein','Direct-mapped','wt',30,2,'1,2MB, 1,44MB',4,'Motherboard',0,8,0,0,0,2,'2,1,0','none','VESA Local Bus','jj','ja','1 Jahr','inklusive',3,0,0,2,'ja','ja');
INSERT INTO PCTEST VALUES ('Bi-Link Desktop i486DX2/66',2665,210,14556.0,4463.0,1789,29.34,30.44,8.82,40.6,0,1,'Tower','ISA',300,6,'Standard socket','nein','Direct-mapped','wb',50,21,'1,2MB, 1,44MB',42,'ISA',1,7,0,0,0,1,'Keine','none','Proprietary Local Bus','jj','nein','1 Jahr','',5,0,2,1,'ja','ja');
INSERT INTO PCTEST VALUES ('BLK 486DX2/66',2995,213,17206.0,8301.0,2654,29.34,34.49,1.76,39.47,0,1,'Minitower','ISA',250,5,'Standard socket','ja','Two-way set-associative','wb',22,1,'1,2MB, 1,44MB',9,'Motherboard',0,8,0,0,0,0,'1,2,0','none','ISA','jj','nein','1 Jahr','inklusive',2,2,0,1,'ja','ja');
INSERT INTO PCTEST VALUES ('Blue Star 466D2U',2949,340,14514.0,4463.0,3701,36.87,35.3,7.98,41.32,0,1,'Desktop','ISA',250,4,'Standard socket','ja','Direct-mapped','wb',21,20,'Dual',37,'ISA',0,8,0,0,1,0,'Keine','none','ISA','jj','ja','1 Jahr','59 $',2,1,2,0,'ja','ja');
INSERT INTO PCTEST VALUES ('BOSS 466d',4495,245,17197.0,8301.0,3183,38.1,41.53,88.83,47.79,1,0,'Minitower','ISA',300,6,'Standard socket','ja','Four-way set-associative','wb',32,10,'1,2MB, 1,44MB',36,'ISA',1,7,0,0,0,0,'1,2,0','none','ISA','jj','nein','2 Jahre','inklusive',3,2,1,0,'ja','ja');
INSERT INTO PCTEST VALUES ('Broadax 486DX2-66',2260,212,14292.0,3920.0,2644,28.0,28.8,2.41,34.78,1,1,'Desktop','ISA',200,5,'Standard socket','nein','Direct-mapped','wb',30,2,'1,2MB, 1,44MB',5,'ISA',2,6,0,0,0,0,'Keine','none','ISA','jj','nein','1 Jahr','80 $',3,0,0,2,'ja','ja');
INSERT INTO PCTEST VALUES ('C² Saber 486/e DX2-66',4145,358,12402.0,8658.0,4998,40.14,40.66,18.09,43.4,1,0,'Tower','EISA',250,6,'Processor card','nein','Direct-mapped','wt',41,40,'1,2MB, 1,44MB',12,'EISA',0,1,6,0,0,0,'Keine','none','ISA','jj','nein','1 Jahr','75 $',4,1,4,0,'ja','ja');
INSERT INTO PCTEST VALUES ('CAF Gold 6D2',2459,213,16869.0,5537.0,2627,25.14,29.11,4.61,42.14,1,0,'Desktop','ISA',200,5,'Standard socket','ja','Direct-mapped','wb',32,2,'1,2MB, 1,44MB',2,'ISA',0,8,0,0,0,0,'Keine','none','Motherboard','jj','nein','1 Jahr','nein',3,2,0,2,'ja','ja');
INSERT INTO PCTEST VALUES ('Clover 486 Quick-I Series',3459,245,16477.0,6124.0,1885,37.76,38.78,6.58,38.27,0,1,'Small-footprint','ISA',200,4,'ZIF socket','ja','Direct-mapped','wb',30,4,'1,2MB, 1,44MB',44,'ISA',0,8,0,0,0,0,'Keine','none','ISA','jj','nein','1 Jahr','inklusive',3,0,0,4,'ja','ja');
INSERT INTO PCTEST VALUES ('Comex 486DX2/66',2750,202,16526.0,6254.0,2766,24.74,29.21,7.63,40.45,1,0,'Desktop','ISA',250,5,'Standard socket','ja','Direct-mapped','wt',21,11,'1,2MB, 1,44MB',1,'ISA',0,8,1,0,0,0,'Keine','none','ISA','jj','nein','2 Jahre','inklusive',2,1,1,1,'ja','ja');
INSERT INTO PCTEST VALUES ('Compaq Deskpro 66M',5192,211,17326.0,8629.0,6159,29.41,34.93,12.34,35.4,1,0,'Desktop','EISA',240,4,'Processor card','ja','Four-way set-associative','wb',30,1,'1,44MB',28,'Motherboard',0,0,5,0,2,0,'1,2,1','none','EISA','jj','ja','1 Jahr','inklusive',3,0,0,1,'ja','ja');
INSERT INTO PCTEST VALUES ('CompuAdd 466E',4213,212,15769.0,3802.0,4865,37.48,34.79,5.65,46.31,1,1,'Desktop','EISA',200,5,'Standard socket','ja','Direct-mapped','wb',21,4,'1,2MB, 1,44MB',18,'EISA',0,0,8,0,0,0,'Keine','none','EISA','jj','nein','1 Jahr','inklusive',2,1,0,4,'ja','ja');
INSERT INTO PCTEST VALUES ('CompuAdd Express 466DX Scalable',2675,200,15125.0,4708.0,9955,27.15,29.66,5.55,27.75,0,1,'Slimline','ISA',150,5,'Standard socket','nein','Direct-mapped','wb',11,1,'1,2MB, 1,44MB',15,'Motherboard',2,3,0,0,0,0,'1,2,0','none','Motherboard','jj','nein','1 Jahr','inklusive',1,1,0,1,'ja','ja');
INSERT INTO PCTEST VALUES ('Comtrade 486 EISA Dream Machine',3095,212,17320.0,9510.0,3466,76.99,65.18,4.21,29.06,1,1,'Tower','EISA',230,5,'Standard socket','nein','Direct-mapped','wb',33,2,'1,2MB, 1,44MB',49,'EISA',0,0,7,0,0,1,'Keine','none','ISA','jj','nein','2 Jahre, lebenslang für Arbeit','inklusive',3,3,0,2,'ja','ja');
INSERT INTO PCTEST VALUES ('Dell 466DE/2',4069,230,16268.0,10155.0,2970,38.12,42.22,7.44,45.45,1,1,'Small-footprint','EISA',224,4,'Standard socket','nein','Direct-mapped','wt',30,1,'1,2MB, 1,44MB',24,'Motherboard',0,0,6,0,0,0,'2,1,1','none','ISA','jj','nein','1 Jahr','inklusive',3,0,0,1,'ja','ja');
INSERT INTO PCTEST VALUES ('DFI 486-66DX2',3982,202,14427.0,3865.0,4395,108.38,54.97,9.42,28.01,1,0,'Tower','ISA',250,5,'Standard socket','nein','Direct-mapped','wb',40,20,'1,2MB, 1,44MB',52,'ISA',0,7,0,0,1,2,'Keine','none','UBSA Local Bus','jj','nein','1 Jahr','inklusive',4,0,2,0,'ja','ja');
INSERT INTO PCTEST VALUES ('Diamond DX2-66',2995,245,17088.0,6917.0,2811,33.48,36.93,7.34,53.15,0,1,'Tower','EISA',200,4,'Standard socket','ja','Direct-mapped','wb',32,2,'1,2MB, 1,44MB',23,'EISA',0,0,8,0,0,0,'Keine','none','ISA','jj','nein','15 Monate, 2 Jahre für Arbeit','75 $',3,2,0,2,'ja','ja');
INSERT INTO PCTEST VALUES ('Digital DECpc 466d2 LP',3124,240,16196.0,4659.0,2106,37.35,36.39,9.02,39.44,1,1,'Slimline','ISA',146,4,'Processor card','nein','Direct-mapped','wb',2,2,'1,44MB',14,'Motherboard',0,3,0,0,0,0,'1,2,1','none','Proprietary Local Bus','jj','nein','1 Jahr','inklusive',0,2,0,2,'ja','ja');
INSERT INTO PCTEST VALUES ('Edge 466 Magnum',3499,213,17139.0,9582.0,2414,208.55,96.58,10.01,141.57,0,1,'Slimline','EISA',200,5,'Standard socket','ja','Direct-mapped','wb',30,2,'Dual',60,'EISA',0,7,0,0,0,1,'Keine','none','Proprietary Local Bus','jn','nein','1 Jahr','inklusive',3,0,0,2,'ja','nein');
INSERT INTO PCTEST VALUES ('EPS ISA 486 DX2/66',2945,340,14181.0,6578.0,2610,36.78,37.76,6.85,42.57,0,1,'Small-footprint','ISA',200,5,'Standard socket','ja','Direct-mapped','wb',21,20,'1,2MB, 1,44MB',23,'ISA',2,6,0,0,0,0,'Keine','none','ISA','jj','nein','3 Jahre','inklusive',2,1,2,0,'ja','ja');
INSERT INTO PCTEST VALUES ('Everex Tempo M Series 486 DX2/66',4563,244,16024.0,7378.0,4473,30.94,35.06,2.92,50.72,1,0,'Desktop','ISA',200,4,'Processor card','nein','Direct-mapped','wb',30,20,'1,2MB, 1,44MB',3,'Motherboard',1,6,0,0,1,0,'1,2,0','none','Motherboard','jj','nein','1 Jahr','inklusive',3,0,2,0,'ja','ja');
INSERT INTO PCTEST VALUES ('Expo 486 dX2/66',2799,245,17244.0,7273.0,2805,39.71,41.8,7.62,51.85,0,1,'Tower','ISA',300,5,'Standard socket','ja','Four-way set-associative','wb',60,20,'1,2MB, 1,44MB',64,'ISA',2,8,0,0,0,0,'Keine','none','ISA','jj','ja','1 Jahr','inklusive',6,0,2,0,'ja','ja');
INSERT INTO PCTEST VALUES ('FCS 486-66',3999,330,16900.0,5071.0,1628,30.2,32.43,5.49,44.26,1,1,'Tower','ISA',300,6,'Standard socket','ja','Direct-mapped','wb',30,20,'1,2MB, 1,44MB',53,'ISA',0,7,0,0,0,0,'Keine','none','Motherboard','jj','nein','1 Jahr, 2 Jahre für Arbeit','keine Angabe',3,0,2,0,'ja','ja');
INSERT INTO PCTEST VALUES ('FutureTech System 462E',4529,425,16653.0,6070.0,2935,102.27,65.53,7.64,60.12,1,1,'Tower','EISA',300,6,'Standard socket','nein','Direct-mapped','wb',60,30,'1,2MB, 1,44MB',47,'EISA',0,0,8,0,0,0,'Keine','none','ISA','jj','nein','15 Monate','79 $',6,0,3,0,'ja','ja');
INSERT INTO PCTEST VALUES ('Gateway 2000 4DX2-66V',2995,340,16400.0,6239.0,6992,30.85,34.32,21.18,53.84,0,1,'Desktop','ISA',200,5,'Standard socket','nein','Two-way set-associative','wt',32,20,'1,2MB, 1,44MB',23,'Motherboard',0,6,0,0,0,2,'2,1,0','none','Motherboard','jj','ja','1 Jahr','inklusive',3,2,2,0,'ja','ja');
INSERT INTO PCTEST VALUES ('GCH EasyData 486DX-2/66',3860,340,13578.0,7084.0,1037,36.23,36.96,5.8,46.05,1,0,'Small-footprint','ISA',220,4,'Standard socket','nein','Direct-mapped','wb',31,1,'1,2MB, 1,44MB',23,'EISA',1,6,0,0,0,0,'Keine','none','EISA','jj','ja','1 Jahr','99 $',3,1,0,1,'ja','ja');
INSERT INTO PCTEST VALUES ('Gecco 466E',3225,240,17199.0,8238.0,3390,32.25,36.97,2.04,44.0,0,1,'Tower','EISA',250,5,'Standard socket','ja','Direct-mapped','wb',40,13,'1,2MB, 1,44MB',65,'EISA',0,2,6,0,0,0,'Keine','none','EISA','jj','nein','2 Jahre','inklusive',4,0,1,3,'ja','ja');
INSERT INTO PCTEST VALUES ('HP Vectra 486/66U',4545,245,15067.0,6589.0,1848,36.14,37.6,8.29,36.89,1,0,'Desktop','EISA',228,5,'LIF socket','nein','Two-way set-associative','pwt',21,0,'1,44MB',61,'Motherboard',0,0,5,0,0,0,'1,2,1','none','Motherboard','jj','nein','1 Jahr','inklusive',2,1,0,0,'ja','ja');
INSERT INTO PCTEST VALUES ('Hyundai 466D2',2721,212,14402.0,4314.0,7215,28.0,29.55,22.66,31.6,1,1,'Small-footprint','ISA',200,6,'ZIF socket','nein','Direct-mapped','wb',32,2,'1,2MB, 1,44MB',21,'Motherboard',0,6,0,0,0,1,'1,2,1','none','VESA Local Bus','nn','nein','18 Monate','inklusive',3,2,0,2,'nein','nein');
INSERT INTO PCTEST VALUES ('IBM PS/2 Model 77 486DX2',5415,400,11525.0,8203.0,3865,104.86,63.75,7.06,40.13,1,0,'Desktop','MCA',200,4,'Standard socket','nein','Nicht verfügbar','n/a',31,0,'2,88MB',58,'Motherboard',0,0,0,5,0,0,'1,2,1','none','MCA','jj','ja','3 Jahre','inklusive',3,1,0,0,'ja','ja');
INSERT INTO PCTEST VALUES ('IDS 466i2',2949,362,17199.0,8301.0,2216,41.42,43.74,7.0,55.3,0,1,'Small-footprint','ISA',200,5,'Standard socket','ja','Direct-mapped','wb',30,2,'Dual',26,'Motherboard',1,7,0,0,0,0,'1,2,0','none','ISA','jj','nein','1 Jahr','95 $',3,0,0,2,'ja','ja');
INSERT INTO PCTEST VALUES ('Insight 486DX2-66I',2999,490,16904.0,5071.0,5124,43.41,41.19,4.88,41.93,0,1,'Desktop','ISA',200,4,'Standard socket','ja','Four-way set-associative','wb',31,1,'1,2MB, 1,44MB',7,'ISA',1,7,0,0,0,0,'Keine','none','ISA','jj','ja','1 Jahr','',3,1,0,1,'ja','ja');
INSERT INTO PCTEST VALUES ('Int. Instr. Blue Max Monolith 486D2/66UP',2797,213,16069.0,5067.0,9504,117.13,64.87,7.97,28.62,1,1,'Desktop','ISA',250,7,'Standard socket','nein','Direct-mapped','wt',40,20,'1,2MB, 1,44MB',13,'ISA',2,5,0,0,1,0,'1,2,0','none','Local Bus','jj','nein','1 Jahr','inklusive',4,0,2,0,'ja','ja');
INSERT INTO PCTEST VALUES ('Keydata 486DX2-66 KeyStation',3195,345,15207.0,7808.0,3281,33.79,37.17,2.98,48.11,0,1,'Tower','EISA',250,4,'Standard socket','ja','Direct-mapped','wt',52,1,'1,2MB, 1,44MB',62,'EISA',0,0,8,0,0,0,'Keine','none','ISA','jj','nein','18 Monate','inklusive',5,2,0,1,'ja','ja');
INSERT INTO PCTEST VALUES ('Lightning ThunderBox',3395,363,16447.0,6270.0,7047,26.71,31.03,18.11,43.22,1,1,'Desktop','ISA',250,6,'Standard socket','nein','Direct-mapped','wt',22,2,'1,2MB, 1,44MB',32,'ISA',0,6,0,0,0,2,'1,2,0','none','VESA Local Bus','jj','nein','2 Jahre','75 $',2,2,0,2,'ja','ja');
INSERT INTO PCTEST VALUES ('LodeStar 486-DX2/66 EISA WINstation',3199,213,14629.0,4667.0,1632,105.38,57.88,6.18,41.82,1,1,'Tower','EISA',250,6,'Standard socket','ja','Direct-mapped','wb',42,23,'1,2MB, 1,44MB',8,'ISA',0,2,6,0,0,0,'Keine','none','ISA','jj','nein','2 Jahre, lebenslang für Arbeit','inklusive',4,2,2,3,'ja','ja');
INSERT INTO PCTEST VALUES ('Mega Impact 486DX2/66E+',3650,340,12341.0,8658.0,2928,52.3,47.35,7.45,40.44,1,1,'Tower','EISA',250,5,'Processor card','nein','Direct-mapped','wt',40,13,'1,2MB, 1,44MB',57,'EISA',0,1,6,0,1,0,'Keine','none','ISA','jj','nein','1 Jahr','',4,0,1,3,'ja','ja');
INSERT INTO PCTEST VALUES ('Memorex Telex 8092-66',4665,213,14514.0,4235.0,2569,29.83,30.55,2.42,33.93,0,1,'Small-footprint','ISA',230,5,'Standard socket','ja','Direct-mapped','wb',30,1,'1,2MB, 1,44MB',19,'Motherboard',0,7,0,0,1,0,'1,2,0','none','ISA','nn','nein','1 Jahr','inklusive',3,0,0,1,'nein','nein');
INSERT INTO PCTEST VALUES ('Micro Express ME 486-Local Bus/DX2/66',3599,240,15818.0,4949.0,10206,150.08,69.21,5.27,87.08,0,1,'Minitower','ISA',200,5,'ZIF socket','nein','Direct-mapped','wb',32,10,'1,2MB, 1,44MB',25,'ISA',0,7,0,0,0,0,'1,1,0','none','Local Bus','jj','nein','2 Jahre','50 $',3,2,1,0,'ja','ja');
INSERT INTO PCTEST VALUES ('Naga Windows Workstation',3195,340,14255.0,7922.0,3094,33.46,36.56,2.76,47.25,0,1,'Desktop','ISA',200,4,'Standard socket','ja','Direct-mapped','wt',32,0,'1,2MB, 1,44MB',6,'ISA',1,8,0,0,1,0,'Keine','none','ISA','jj','ja','2 Jahre, lebenslang für Arbeit','inklusive',3,2,0,0,'ja','ja');
INSERT INTO PCTEST VALUES ('National Microsystems Flash 486DX2-66E',3999,312,15022.0,4676.0,2315,108.18,59.31,10.32,54.99,0,1,'Tower','EISA',300,5,'Standard socket','ja','Direct-mapped','wb',50,23,'1,2MB, 1,44MB',6,'EISA',0,0,7,0,0,1,'Keine','none','Local Bus','jj','nein','15 Monate, 2 Jahre für Arbeit','50 $',5,0,2,3,'ja','ja');
INSERT INTO PCTEST VALUES ('NCR System 3350',4600,340,16346.0,10292.0,5326,32.85,38.26,4.24,46.3,1,1,'Small-footprint','MCA',182,4,'Processor card','nein','Two-way set-associative','wt',11,2,'1,44MB',43,'Motherboard',0,0,0,4,2,0,'1,1,1','none','Motherboard','jj','nein','2 Jahre','inklusive',1,1,0,2,'ja','ja');
INSERT INTO PCTEST VALUES ('NEC Express DX2/66e',4599,535,14739.0,4581.0,5908,21.84,25.24,4.17,50.22,1,0,'Desktop','EISA',285,6,'Processor card','nein','Four-way set-associative','wb',21,20,'1,44MB',63,'Motherboard',0,0,5,0,1,0,'1,2,0','none','Motherboard','jj','nein','1 Jahr','inklusive',2,1,2,0,'ja','ja');
INSERT INTO PCTEST VALUES ('NETiS Ultra WinStation N466L',2300,213,16070.0,5067.0,9489,93.48,59.67,5.06,38.24,1,1,'Desktop','ISA',200,5,'Standard socket','nein','Four-way set-associative','wb',11,1,'1,2MB, 1,44MB',10,'ISA',2,5,0,0,1,1,'Keine','none','Proprietary Local Bus','jj','nein','1 Jahr','80 $',1,1,0,1,'ja','ja');
INSERT INTO PCTEST VALUES ('Northgate SlimLine ZXP',3197,245,11598.0,5991.0,6602,26.0,28.01,6.87,51.69,0,1,'Slimline','ISA',150,3,'ZIF socket','nein','Nicht verfügbar','n/a',11,10,'Dual',30,'Motherboard',2,3,0,0,0,0,'1,2,0','none','Motherboard','jj','nein','1 Jahr','inklusive',1,1,1,0,'ja','ja');
INSERT INTO PCTEST VALUES ('Occidental 66MHz 486DX2',2895,245,11814.0,4414.0,4661,28.37,28.98,6.33,41.72,0,1,'Tower','ISA',200,7,'ZIF socket','nein','Nicht verfügbar','n/a',40,24,'1,2MB, 1,44MB',51,'Motherboard',1,7,0,0,0,1,'1,2,0','none','VESA Local Bus','jj','nein','1 Jahr','39 $',4,0,2,4,'ja','ja');
INSERT INTO PCTEST VALUES ('Osicom i466 MOD 420',3795,420,14234.0,3588.0,1357,44.43,26.43,6.03,46.28,1,1,'Small-footprint','ISA',200,5,'Standard socket','ja','Direct-mapped','wb',31,1,'1,2MB, 1,44MB',31,'ISA',2,6,0,0,0,0,'Keine','none','ISA','jj','nein','1 Jahr','inklusive',3,1,0,1,'ja','ja');
INSERT INTO PCTEST VALUES ('PC Brand Leader Cache 486/DX2-66',2545,170,16018.0,9522.0,3271,22.85,28.72,1.76,27.94,0,1,'Desktop','ISA',200,5,'Standard socket','ja','Direct-mapped','wt',22,0,'1,2MB, 1,44MB',33,'Motherboard',1,4,0,0,0,0,'1,2,0','none','Motherboard','jj','nein','1 Jahr','inklusive',2,2,0,0,'ja','ja');
INSERT INTO PCTEST VALUES ('PC Pros 486/66DX2 5550T',2999,340,17190.0,8310.0,2523,27.37,32.75,12.62,39.85,0,1,'Tower','ISA',300,4,'Standard socket','nein','Direct-mapped','wb',60,3,'1,2MB, 1,44MB',55,'ISA',1,7,0,0,0,0,'1,2,0','none','ISA','jj','nein','2 Jahre','29 $',6,0,0,3,'ja','ja');
INSERT INTO PCTEST VALUES ('PCS Double Pro-66',3895,245,17190.0,8301.0,2677,37.61,41.1,1.94,45.4,0,1,'Desktop','ISA',200,4,'Standard socket','nein','Direct-mapped','wb',31,0,'1,2MB, 1,44MB',17,'Motherboard',1,7,0,0,0,0,'1,1,0','none','ISA','jj','nein','2 Jahre','',3,1,0,0,'ja','ja');
INSERT INTO PCTEST VALUES ('Poly 486-66LM',3300,320,17081.0,6114.0,1663,44.8,43.28,7.68,46.11,1,1,'Tower','ISA',250,5,'Standard socket','ja','Four-way set-associative','wb',32,1,'1,2MB, 1,44MB',46,'ISA',0,7,0,0,1,0,'Keine','none','Proprietary Local Bus','jj','nein','2 Jahre, 5 Jahre für Arbeit','75 $',3,2,0,1,'ja','ja');
INSERT INTO PCTEST VALUES ('QSI Klonimus 486DX2/66',3650,340,16870.0,5537.0,2622,23.23,27.48,6.98,38.92,0,1,'Small-footprint','ISA',200,4,'Standard socket','ja','Direct-mapped','wb',31,10,'1,2MB, 1,44MB',4,'ISA',2,6,0,0,0,0,'Keine','none','ISA','jj','nein','2 Jahre','inklusive',3,1,1,0,'ja','ja');
INSERT INTO PCTEST VALUES ('Quill Qtech 486 4D2/66',3520,334,14259.0,3924.0,1269,24.59,26.25,5.27,35.95,0,1,'Small-footprint','ISA',230,5,'Standard socket','nein','Direct-mapped','wb',32,0,'1,2MB, 1,44MB',4,'ISA',2,5,0,0,0,0,'Keine','none','ISA','jj','ja','1 Jahr','inklusive',3,2,0,0,'ja','ja');
INSERT INTO PCTEST VALUES ('Silicon Pylon II 486DXi-212',2895,212,16731.0,4767.0,4633,30.09,32.21,6.76,40.11,1,1,'Minitower','ISA',220,4,'Standard socket','nein','Two-way set-associative','wb',33,1,'1,2MB, 1,44MB',11,'ISA',0,8,0,0,0,0,'Keine','none','ISA','jj','nein','1 Jahr','',3,3,0,1,'ja','ja');
INSERT INTO PCTEST VALUES ('SST 486DX2-66MWC',2800,212,16247.0,6075.0,1249,35.36,36.78,6.59,43.79,1,1,'Desktop','ISA',200,2,'Standard socket','nein','Direct-mapped','wb',31,0,'Dual',26,'ISA',1,7,0,0,0,0,'Keine','none','ISA','jj','ja','1 Jahr','inklusive',3,1,0,0,'ja','ja');
INSERT INTO PCTEST VALUES ('Standard Windows Workstation Plus',3845,240,10807.0,7329.0,2764,26.24,29.11,7.23,36.79,0,1,'Desktop','EISA',200,4,'Processor card','nein','Direct-mapped','wt',31,1,'1,2MB, 1,44MB',48,'EISA',0,1,6,0,0,0,'Keine','none','ISA','jj','ja','2 Jahre','inklusive',3,1,0,1,'ja','ja');
INSERT INTO PCTEST VALUES ('Swan 486DX2-66DB',4684,318,15705.0,4603.0,2428,26.57,29.07,10.24,36.55,0,1,'Desktop','ISA',200,5,'Processor card','nein','Direct-mapped','wb',50,1,'1,2MB, 1,44MB',45,'Motherboard',0,6,0,0,0,0,'1,2,0','none','Proprietary Local Bus','jj','nein','2 Jahre','inklusive',5,0,0,1,'ja','ja');
INSERT INTO PCTEST VALUES ('Tangent Model 466ex',3966,340,17279.0,9416.0,3406,98.28,73.33,9.56,26.43,0,1,'Tower','EISA',200,4,'Standard socket','ja','Direct-mapped','wb',32,1,'Dual',54,'EISA',0,2,6,0,0,0,'Keine','none','ISA','jj','nein','1 Jahr','inklusive',3,2,0,1,'ja','ja');
INSERT INTO PCTEST VALUES ('Tri-Star 66/DX2-VL',3569,340,15452.0,5586.0,1754,27.98,30.96,20.09,47.24,1,1,'Tower','ISA',300,8,'Standard socket','nein','Direct-mapped','wt',40,40,'1,2MB, 1,44MB',56,'Motherboard',0,6,0,0,0,2,'1,2,0','none','VESA Local Bus','jj','nein','2 Jahre, lebenslang für Arbeit','inklusive',4,0,4,0,'ja','ja');
INSERT INTO PCTEST VALUES ('Twinhead Superset 600/462D',2999,212,12462.0,5405.0,3097,22.91,26.1,4.61,33.46,1,1,'Small-footprint','ISA',150,5,'Standard socket','nein','Direct-mapped','wt',21,1,'1,44MB',40,'Motherboard',0,4,0,0,0,0,'1,2,1','none','Motherboard','jj','nein','1 Jahr','inklusive',2,1,0,1,'ja','ja');
INSERT INTO PCTEST VALUES ('U.S. Micro Jet 486DX2-66',3450,336,12051.0,7930.0,1146,36.1,37.92,5.61,47.61,0,1,'Desktop','ISA',230,5,'Standard socket','ja','Direct-mapped','wt',32,0,'1,2MB, 1,44MB',4,'ISA',0,7,0,0,1,0,'Keine','none','ISA','jj','nein','1 Jahr, 2 Jahre für Arbeit','195 $',3,2,0,0,'ja','ja');
INSERT INTO PCTEST VALUES ('USA Flex 486DX2/66',3799,248,14259.0,5057.0,1547,30.12,30.68,6.66,42.95,0,1,'Tower','EISA',250,6,'Standard socket','nein','Direct-mapped','wb',50,10,'1,2MB, 1,44MB',35,'EISA',0,0,7,0,0,0,'Keine','none','ISA','jj','nein','1 Jahr','inklusive',5,0,1,0,'ja','ja');
INSERT INTO PCTEST VALUES ('Wyse Decision 486si',3299,213,15608.0,4962.0,9770,29.91,32.11,5.33,36.5,1,0,'Slimline','ISA',200,5,'ZIF socket','ja','Direct-mapped','wb',23,0,'1,2MB, 1,44MB',38,'Motherboard',1,5,0,0,0,0,'1,2,1','none','Proprietary Local Bus','jj','nein','1 Jahr','99 $',2,3,0,0,'ja','ja');
INSERT INTO PCTEST VALUES ('ZDS Z-Station 466Xh Model 200',4095,200,10806.0,6905.0,4298,37.78,36.68,6.89,44.07,1,1,'Slimline','ISA',200,4,'Standard socket','ja','Nicht verfügbar','n/a',2,30,'1,44MB',39,'Motherboard',0,4,0,0,0,0,'1,1,1','Ethernet','Motherboard','jj','ja','1 Jahr','',0,2,3,0,'ja','ja');
INSERT INTO PCTEST VALUES ('Zeos 486DX2-66',2995,245,15067.0,4152.0,1967,27.69,29.08,42.64,47.75,0,1,'Desktop','ISA',300,5,'ZIF socket','nein','Direct-mapped','wt',22,21,'1,2MB, 1,44MB',29,'Motherboard',1,7,0,0,0,2,'1,2,0','none','VESA Local Bus','jj','ja','1 Jahr','49 $',2,2,2,1,'ja','ja');
