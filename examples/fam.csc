FORMAL_CONTEXT
   Geschlecht =
      TITLE ""
      OBJECTS
         0 O0 "O0"
         1 O1 "O1"
      ATTRIBUTES
         0 A0 "weiblich"
         1 A1 "männlich"
      RELATION
         2, 2
            *.
            .*
   ;

LINE_DIAGRAM
   Geschlecht =
      TITLE ""
      UNITLENGTH 1 mm
      POINTS
         0 0 0
         1 10 -10
         2 -10 -10
         3 0 -20
      LINES
         (0, 1)
         (0, 2)
         (1, 3)
         (2, 3)
      OBJECTS
         1 O1 "O1" ",,,,(3,-3),l"
         2 O0 "O0" ",,,,(-1.13333,-4.3),r"
      ATTRIBUTES
         1 A1 "männlich" ",,,,(3.93333,4.3),l"
         2 A0 "weiblich" ",,,,(-3.23333,4.6),r"
      CONCEPTS
   ;

FORMAL_CONTEXT
   Status =
      TITLE ""
      OBJECTS
         0 O0 "O0"
         1 O1 "O1"
         2 O2 "O2"
      ATTRIBUTES
         0 A0 "schüler"
         1 A1 "student"
         2 A2 "beruf"
      RELATION
         3, 3
            *..
            .*.
            ..*
   ;

LINE_DIAGRAM
   Status =
      TITLE ""
      UNITLENGTH 1 mm
      POINTS
         0 0 0
         1 20 -20
         2 0 -20
         3 -20 -20
         4 0 -40
      LINES
         (0, 1)
         (0, 2)
         (0, 3)
         (1, 4)
         (2, 4)
         (3, 4)
      OBJECTS
         1 O2 "O2" ",,,,(3,-3),l"
         2 O1 "O1" ",,,,(3,-3),l"
         3 O0 "O0" ",,,,(-5.2,-5.1),r"
      ATTRIBUTES
         1 A2 "beruf" ",,,,(7.5,4.6),l"
         2 A1 "student" ",,,,(13.8,13),l"
         3 A0 "schüler" ",,,,(-6.7,4.1),r"
      CONCEPTS
   ;
