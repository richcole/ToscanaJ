// csc2csx.cpp
// Bernd Groh
// 28.03.2001

#include <fca\fcacore.h>
#include <fca\concfile.h>
#include <fca\formats.h>
#include <stdio.h>

#define PARSE_OK 0
#define WRONG_NUMBER_OF_ARGUMENTS 1
#define INVALID_FILE_NAME 2
#define INVALID_FILE_TYPE 3
#define NO_TARGET_FILE 4
#define NO_SOURCE_FILE 5
#define UNKNOWN_OPTION 6
#define PATH_AND_DSN 7
#define TABLE_KEY_AND_QUERY 8
#define OPTION_DEFINED 9
#define DOES_NOT_TAKE_VALUE 10
#define MUST_TAKE_VALUE 11
#define CANNOT_OPEN_FILE 12
#define CANNOT_OPEN_INCLUDEFILE 13
#define SOURCE_IS_EMPTY 14
#define SOURCE_IS_INCONSISTENT 15
#define FILE_CORRUPT 16

char versionDefault[] = "1.0";
char encodingDefault[] = "ISO-8859-1";
char schemaInstance[] = "http://www.w3.org/2000/10/XMLSchema-instance";
char schemaDefault[] = "http://meganesia.int.gu.edu.au/projects/ToscanaJ/schemas/csx.xsd";

int error(uint errflag, char* path = 0);
int isWhere(const char* objName);
void getObjectDescription(char*, TObject&, TFCAFile*, TLineDiagram*, int generateQuery = 0);
void getAttributeDescription(char*, TAttribute&, TFCAFile*, TLineDiagram*);
void getObjectDescription(char*, TObject&, TConcreteScale*);
void getAttributeDescription(char*, TAttribute&, TConcreteScale*);
int checkAbstractScalesInConcreteScales(TFCAFile* csc, int generateQuery);
int checkDiagramsInAbstractScales(TFCAFile* csc);
int checkForUniqueDiagramInAbstractScales(TFCAFile* csc);
int checkConscriptFile(TFCAFile* csc, int generateQuery);
int parseDiagrams(int argc, char* argv[]);
int parseConceptual(int argc, char* argv[]);
void strxml(char*);
void strxmlcat(char*, const char*);
void strxmlcpy(char*, const char*);

// -----------------------------------------------------------------------------
//
int main(int argc, char* argv[])
{
	return parseConceptual(argc, argv);
}

// -----------------------------------------------------------------------------
//
int parseConceptual(int argc, char* argv[])
{
	// check whether module is part of the argument string
   // program only works with the initial name to ensure
   // program name retains intended meaning

   char *dest;
	int start = 0;
   if (argv[start][0] != '-')
   {
   	dest = strrchr(argv[start], '\\');
      if (dest == 0)
      	dest = argv[start];
      else
      	dest++;
      if ((stricmp(dest, "csc2csx.exe") == 0)
      	|| (stricmp(dest, "csc2csx") == 0))
         start++;
      else
      	return error(FILE_CORRUPT);
   }

	if (argc == start)
   	return error(WRONG_NUMBER_OF_ARGUMENTS);

   // check whether last argument contains the *.csx (xml) target name

   int stop = argc-1;
   char target[MAX_PATH] = "";
   char *source = 0;

   if (stop > start)
   {
   	if (argv[stop][0] == '-')
      	return error(NO_SOURCE_FILE, argv[stop]);

	   dest = strrchr(argv[stop], '.');
	   if (dest == 0)
	   {
      	if (argv[stop-1][0] != '-')
         {
         	// it is target name

            strcpy(target, argv[stop]);
            strcat(target, ".csx");
            stop--;
         }
	   }
	   else
	   {
	   	if (stricmp(dest, ".csx") == 0)
	      {
	      	strcpy(target, argv[stop]);
	         stop--;
	      }
	      else if (stricmp(dest, ".csc") == 0)
	      {
	         dest = strrchr(argv[stop], '\\');
	         if (dest != 0)
	         	strcpy(target, dest + 1);
	         else
	         	strcpy(target, argv[stop]);
	         dest = strrchr(target, '.');
	         strcpy(dest, ".csx");
	         source = argv[stop];
	         stop--;
	      }
         else
	      {
         	// other target types are accepted

	      	strcpy(target, argv[stop]);
	         stop--;
	      }
      }
   }

   // check whether second last argument contains the *.csc source name
   // only if source is not already found yet

   if (source == 0)
   {
   	if (argv[stop][0] == '-')
      	return error(NO_SOURCE_FILE, argv[stop]);

      dest = strrchr(argv[stop], '.');
      if (dest == 0)
      {
	   	// assume it is source name

         if (*target == 0)
         {
		      strcpy(target, argv[stop]);
		      strcat(target, ".csx");
         }

         source = target + strlen(target) + 1;
	      strcpy(source, argv[stop]);
	      strcat(source, ".csc");
         stop--;
	   }
      else
      {
        	if (stricmp(dest, ".csc") == 0)
         {
         	source = argv[stop];
            stop--;

            if (*target == 0)
            {
            	strcpy(target, argv[stop]);
               dest = strrchr(target, '.');
               strcpy(dest, ".csx");
            }
         }
         else
         	return error(INVALID_FILE_TYPE, argv[stop]);
      }
   }

   // check and set all options

   char *dsn = 0, *path = 0, *table = 0, *key = 0, *query = 0;
   char *program = 0, *stylefile = 0, *open = 0, *close = 0;
   char *schema = 0, *include = 0, *encoding = 0, *version = 0;
   int l = start, check = 0, msg = 0, askdb = 0, usedb = 0;

   while (l <= stop)
   {
   	if (strnicmp(argv[l], "-dd=", 4) == 0)
      	if (dsn != 0)
         	return error(OPTION_DEFINED, argv[l]);
         else if (path != 0)
         	return error(PATH_AND_DSN, argv[l]);
         else
         	dsn = argv[l] + 4;
   	if (stricmp(argv[l], "-dd") == 0)
      	if (dsn != 0)
         	return error(OPTION_DEFINED, argv[l]);
         else if (path != 0)
         	return error(PATH_AND_DSN, argv[l]);
         else
         	return error(MUST_TAKE_VALUE, argv[l]);
      else if (strnicmp(argv[l], "-dp=", 4) == 0)
      	if (path != 0)
         	return error(OPTION_DEFINED, argv[l]);
         else if (dsn != 0)
         	return error(PATH_AND_DSN, argv[l]);
         else
         	path = argv[l] + 4;
      else if (stricmp(argv[l], "-dp") == 0)
      	if (path != 0)
         	return error(OPTION_DEFINED, argv[l]);
         else if (dsn != 0)
         	return error(PATH_AND_DSN, argv[l]);
         else
         	return error(MUST_TAKE_VALUE, argv[l]);
      else if (strnicmp(argv[l], "-dt=", 4) == 0)
      	if (table != 0)
         	return error(OPTION_DEFINED, argv[l]);
         else if (query != 0)
         	return error(TABLE_KEY_AND_QUERY, argv[l]);
         else
         	table = argv[l] + 4;
      else if (stricmp(argv[l], "-dt") == 0)
      	if (table != 0)
         	return error(OPTION_DEFINED, argv[l]);
         else if (query != 0)
         	return error(TABLE_KEY_AND_QUERY, argv[l]);
         else
         	return error(MUST_TAKE_VALUE, argv[l]);
      else if (strnicmp(argv[l], "-dk=", 4) == 0)
      	if (key != 0)
         	return error(OPTION_DEFINED, argv[l]);
         else if (query != 0)
         	return error(TABLE_KEY_AND_QUERY, argv[l]);
         else
         	key = argv[l] + 4;
      else if (stricmp(argv[l], "-dk") == 0)
      	if (key != 0)
         	return error(OPTION_DEFINED, argv[l]);
         else if (query != 0)
         	return error(TABLE_KEY_AND_QUERY, argv[l]);
         else
         	return error(MUST_TAKE_VALUE, argv[l]);
      else if (strnicmp(argv[l], "-dq=", 4) == 0)
      	if (query != 0)
         	return error(OPTION_DEFINED, argv[l]);
         else if ((table != 0) || (key != 0))
         	return error(TABLE_KEY_AND_QUERY, argv[l]);
         else
         	query = argv[l] + 4;
      else if (stricmp(argv[l], "-dq") == 0)
      	if (query != 0)
         	return error(OPTION_DEFINED, argv[l]);
         else if ((table != 0) || (key != 0))
         	return error(TABLE_KEY_AND_QUERY, argv[l]);
         else
         	return error(MUST_TAKE_VALUE, argv[l]);
      else if (strnicmp(argv[l], "-vp=", 4) == 0)
      	if (program != 0)
         	return error(OPTION_DEFINED, argv[l]);
         else
         	program = argv[l] + 4;
      else if (stricmp(argv[l], "-vp") == 0)
      	if (program != 0)
         	return error(OPTION_DEFINED, argv[l]);
         else
         	return error(MUST_TAKE_VALUE, argv[l]);
      else if (strnicmp(argv[l], "-vs=", 4) == 0)
      	if (stylefile != 0)
         	return error(OPTION_DEFINED, argv[l]);
         else
         	stylefile = argv[l] + 4;
      else if (stricmp(argv[l], "-vs") == 0)
      	if (stylefile != 0)
         	return error(OPTION_DEFINED, argv[l]);
         else
         	return error(MUST_TAKE_VALUE, argv[l]);
      else if (strnicmp(argv[l], "-vo=", 4) == 0)
      	if (open != 0)
         	return error(OPTION_DEFINED, argv[l]);
         else
         	open = argv[l] + 4;
      else if (stricmp(argv[l], "-vo") == 0)
      	if (open != 0)
         	return error(OPTION_DEFINED, argv[l]);
         else
         	return error(MUST_TAKE_VALUE, argv[l]);
      else if (strnicmp(argv[l], "-vc=", 4) == 0)
      	if (close != 0)
         	return error(OPTION_DEFINED, argv[l]);
         else
         	close = argv[l] + 4;
      else if (stricmp(argv[l], "-vc") == 0)
      	if (close != 0)
         	return error(OPTION_DEFINED, argv[l]);
         else
         	return error(MUST_TAKE_VALUE, argv[l]);
      else if (stricmp(argv[l], "-db") == 0)
      	if (askdb != 0)
         	return error(OPTION_DEFINED, argv[l]);
         else
         	askdb = 1;
      else if (strnicmp(argv[l], "-db=", 4) == 0)
      	if (askdb != 0)
         	return error(OPTION_DEFINED, argv[l]);
         else
         	return error(DOES_NOT_TAKE_VALUE, argv[l]);
      else if (strnicmp(argv[l], "-xsi=", 5) == 0)
      	if (schema != 0)
         	return error(OPTION_DEFINED, argv[l]);
         else
         	schema = argv[l] + 5;
      else if (stricmp(argv[l], "-xsi") == 0)
      	if (schema != 0)
         	return error(OPTION_DEFINED, argv[l]);
         else
         	schema = schemaDefault;
      else if (strnicmp(argv[l], "-inc=", 5) == 0)
      	if (include != 0)
         	return error(OPTION_DEFINED, argv[l]);
         else
         	include = argv[l] + 5;
      else if (stricmp(argv[l], "-inc") == 0)
      	if (include != 0)
         	return error(OPTION_DEFINED, argv[l]);
         else
         	return error(MUST_TAKE_VALUE, argv[l]);
      else if (strnicmp(argv[l], "-enc=", 5) == 0)
      	if (encoding != 0)
         	return error(OPTION_DEFINED, argv[l]);
         else
         	encoding = argv[l] + 5;
      else if (stricmp(argv[l], "-enc") == 0)
      	if (encoding != 0)
         	return error(OPTION_DEFINED, argv[l]);
         else
         	encoding = encodingDefault;
      else if (strnicmp(argv[l], "-ver=", 5) == 0)
      	if (version != 0)
         	return error(OPTION_DEFINED, argv[l]);
         else
         	version = argv[l] + 5;
      else if (stricmp(argv[l], "-ver") == 0)
      	if (version != 0)
         	return error(OPTION_DEFINED, argv[l]);
         else
         	version = versionDefault;
      else if (stricmp(argv[l], "-c") == 0)
      	if (check != 0)
         	return error(OPTION_DEFINED, argv[l]);
         else
         	check = 1;
      else if (strnicmp(argv[l], "-c=", 3) == 0)
      	if (check != 0)
         	return error(OPTION_DEFINED, argv[l]);
         else
         	return error(DOES_NOT_TAKE_VALUE, argv[l]);
      else if (stricmp(argv[l], "-m") == 0)
      	if (msg != 0)
         	return error(OPTION_DEFINED, argv[l]);
         else
         	msg = 1;
      else if (strnicmp(argv[l], "-m=", 3) == 0)
      	if (msg != 0)
         	return error(OPTION_DEFINED, argv[l]);
         else
         	return error(DOES_NOT_TAKE_VALUE, argv[l]);
      else
      	return error(UNKNOWN_OPTION, argv[l]);

      l++;
   }

   // all options have been proceeded successfully
   // assume that combinations of options are valid

   // open source file (conscript *.csc) for read access

   TFCAFile* csc = new TFCAFile();
   if (include != 0)
   	csc->SetIncludePath(include);
   csc->SetName(source);
   int errOK = csc->ReadFile(check, msg, 0);
   if (errOK != ERR_OK)
   {
   	delete csc;
      return error(CANNOT_OPEN_FILE, source);
   }
   errOK = csc->ReadNewIncludeFiles(check, msg);
   if (errOK != ERR_OK)
   {
   	delete csc;
      return error(CANNOT_OPEN_INCLUDEFILE, source);
   }

   // check whether csc file contains at most one (1!) conceptual scheme

	TIFCAArray* scheme = csc->GetListOfConceptualSchemes();
   if (scheme->GetItemsInContainer() > 1)
   {
   	delete scheme;
   	delete csc;
      return error(SOURCE_IS_INCONSISTENT, source);
   }

   // check whether csc file contains database entry

   TDatabase* cscdb = 0;
   if (scheme->GetItemsInContainer() == 1)
   	cscdb = ((TConceptualScheme*)((*scheme)[0]))->GetDatabase();
   if ((dsn != 0) || (path != 0) || (table != 0) || (key != 0) || (query != 0)
      || ((cscdb != 0) && ((cscdb->GetDatabase() != "")
      || (cscdb->GetView() != "") || (cscdb->GetPrimaryKey() != ""))))
   {
      usedb = 1;
      askdb = 1;		// if yes, or any database entry was given, askDatabase becomes default
   }

   // check wheter csc file contains valid data for parsing

   errOK = checkConscriptFile(csc, askdb);
   if (errOK != ERR_OK)
   {
   	delete scheme;
   	delete csc;
      return error(SOURCE_IS_EMPTY, source);
   }
	TIFCAArray* concrete = csc->GetListOfConcreteScales();
   if (concrete->GetItemsInContainer() == 0)
   {
   	delete concrete;
   	delete scheme;
   	delete csc;
      return error(SOURCE_IS_EMPTY, source);
   }

   // csc file contains concrete scales
   // open target file for write access

   ofstream csx(target);
   if (csx.bad())
   {
   	delete concrete;
   	delete scheme;
      delete csc;
   	return error(CANNOT_OPEN_FILE, target);
   }

   // proceed to parse csc to csx
   // write header -- use buffer for writing to file

   char buffer[1024];

   strcpy(buffer, "<?xml version=\"");
   if (version != 0)
   	strcat(buffer, version);
   else
   	strcat(buffer, versionDefault);
   strcat(buffer, "\" encoding=\"");
   if (encoding != 0)
   	strcat(buffer, encoding);
   else
   	strcat(buffer, encodingDefault);
   strcat(buffer, "\"?>");
   csx.write(buffer, strlen(buffer));
   csx.put('\n');

   // write schema header

   strcpy(buffer, "<conceptualSchema version=\"1.0\" askDatabase=\"");
   if (askdb != 0)
   	strcat(buffer, "true");
   else
   	strcat(buffer, "false");
   strcat(buffer, "\"");
   if (schema != 0)
   {
   	strcat(buffer, " xmlns:xsi=\"");
   	strcat(buffer, schemaInstance);
   	strcat(buffer, "\" xsi:noNamespaceSchemaLocation=\"");
      strcat(buffer, schema);
      strcat(buffer, "\"");
   }
   strcat(buffer, ">");
   csx.write(buffer, strlen(buffer));
   csx.put('\n');

   // write database (optional)

   if (usedb != 0)
   {
   	csx.put('\t');
      strcpy(buffer, "<database>");
      csx.write(buffer, strlen(buffer));
      csx.put('\n');

      if (dsn != 0)
      {
      	csx.put('\t');
         csx.put('\t');
         strcpy(buffer, "<dsn>");
         strxmlcat(buffer, dsn);
         strcat(buffer, "</dsn>");
         csx.write(buffer, strlen(buffer));
         csx.put('\n');
      }
      else if ((cscdb->GetDatabase() != "") && (path == 0))
      {
      	csx.put('\t');
         csx.put('\t');
         strcpy(buffer, "<dsn>");
         strxmlcat(buffer, cscdb->GetDatabase().c_str());
         strcat(buffer, "</dsn>");
         csx.write(buffer, strlen(buffer));
         csx.put('\n');
      }

      if (path != 0)
      {
      	csx.put('\t');
         csx.put('\t');
         strcpy(buffer, "<path>");
         strxmlcat(buffer, path);
         strcat(buffer, "</path>");
         csx.write(buffer, strlen(buffer));
         csx.put('\n');
      }

      if (table != 0)
      {
      	csx.put('\t');
         csx.put('\t');
         strcpy(buffer, "<table>");
         strxmlcat(buffer, table);
         strcat(buffer, "</table>");
         csx.write(buffer, strlen(buffer));
         csx.put('\n');
      }
      else if ((cscdb->GetView() != "") && (query == 0))
      {
      	csx.put('\t');
         csx.put('\t');
         strcpy(buffer, "<table>");
         strxmlcat(buffer, cscdb->GetView().c_str());
         strcat(buffer, "</table>");
         csx.write(buffer, strlen(buffer));
         csx.put('\n');
      }

      if (key != 0)
      {
      	csx.put('\t');
         csx.put('\t');
         strcpy(buffer, "<key>");
         strxmlcat(buffer, key);
         strcat(buffer, "</key>");
         csx.write(buffer, strlen(buffer));
         csx.put('\n');
      }
      else if ((cscdb->GetPrimaryKey() != "") && (query == 0))
      {
      	csx.put('\t');
         csx.put('\t');
         strcpy(buffer, "<key>");
         strxmlcat(buffer, cscdb->GetPrimaryKey().c_str());
         strcat(buffer, "</key>");
         csx.write(buffer, strlen(buffer));
         csx.put('\n');
      }

      if (query != 0)
      {
      	csx.put('\t');
         csx.put('\t');
         strcpy(buffer, "<query>");
         strxmlcat(buffer, query);
         strcat(buffer, "</query>");
         csx.write(buffer, strlen(buffer));
         csx.put('\n');
      }

   	csx.put('\t');
      strcpy(buffer, "</database>");
      csx.write(buffer, strlen(buffer));
      csx.put('\n');
   }

   // write viewer (optional)

   if ((program != 0) || (stylefile != 0) || (open != 0) || (close != 0))
   {
   	csx.put('\t');
      strcpy(buffer, "<viewer>");
      csx.write(buffer, strlen(buffer));
      csx.put('\n');

      if (program != 0)
      {
      	csx.put('\t');
         csx.put('\t');
         strcpy(buffer, "<program>");
         strxmlcat(buffer, program);
         strcat(buffer, "</program>");
         csx.write(buffer, strlen(buffer));
         csx.put('\n');
      }

      if (stylefile != 0)
      {
      	csx.put('\t');
         csx.put('\t');
         strcpy(buffer, "<template>");
         strxmlcat(buffer, stylefile);
         strcat(buffer, "</template>");
         csx.write(buffer, strlen(buffer));
         csx.put('\n');
      }

      if (open != 0)
      {
      	csx.put('\t');
         csx.put('\t');
         strcpy(buffer, "<openDelimiter>");
         strxmlcat(buffer, open);
         strcat(buffer, "</openDelimiter>");
         csx.write(buffer, strlen(buffer));
         csx.put('\n');
      }

      if (close != 0)
      {
      	csx.put('\t');
         csx.put('\t');
         strcpy(buffer, "<closeDelimiter>");
         strxmlcat(buffer, close);
         strcat(buffer, "</closeDelimiter>");
         csx.write(buffer, strlen(buffer));
         csx.put('\n');
      }

   	csx.put('\t');
      strcpy(buffer, "</viewer>");
      csx.write(buffer, strlen(buffer));
      csx.put('\n');
   }

   // write context (required)

   csx.put('\t');
   strcpy(buffer, "<context>");
   csx.write(buffer, strlen(buffer));
   csx.put('\n');

   // write objects

   uint id = 1;
   uint d, dc = concrete->GetItemsInContainer();
   TLineDiagram* cDiagram;
   for (d = 0; d < dc; d++)
   {
   	cDiagram = ((TConcreteScale*)((*concrete)[d]))
      	->GetAbstractScale()->GetDiagram(0);
      const TQSObjectArray& objects = cDiagram->GetObjects();
      uint o, oc = objects.GetItemsInContainer();
      for (o = 0; o < oc; o++)
      {
      	csx.put('\t');
         csx.put('\t');
         strcpy(buffer, "<object id=\"");
         itoa(id, buffer + strlen(buffer), 10);
         strcat(buffer, "\">");
         getObjectDescription(buffer + strlen(buffer), objects[o], (TConcreteScale*)((*concrete)[d]));
         strcat(buffer, "</object>");
         csx.write(buffer, strlen(buffer));
         csx.put('\n');
         id++;
      }
   }

   // write attributes

   id = 1;
   for (d = 0; d < dc; d++)
   {
   	cDiagram = ((TConcreteScale*)((*concrete)[d]))
      	->GetAbstractScale()->GetDiagram(0);
      const TQSAttributeArray& attributes = cDiagram->GetAttributes();
      uint a, ac = attributes.GetItemsInContainer();
      for (a = 0; a < ac; a++)
      {
      	csx.put('\t');
         csx.put('\t');
         strcpy(buffer, "<attribute id=\"");
         itoa(id, buffer + strlen(buffer), 10);
         strcat(buffer, "\">");
         getAttributeDescription(buffer + strlen(buffer), attributes[a], (TConcreteScale*)((*concrete)[d]));
         strcat(buffer, "</attribute>");
         csx.write(buffer, strlen(buffer));
         csx.put('\n');
         id++;
      }
   }

   csx.put('\t');
   strcpy(buffer, "</context>");
   csx.write(buffer, strlen(buffer));
   csx.put('\n');

   // write diagram (optional but given)

   int aid = 1, oid = 1;
   for (d = 0; d < dc; d++)
   {
   	cDiagram = ((TConcreteScale*)((*concrete)[d]))
      	->GetAbstractScale()->GetDiagram(0);

      // write diagram header

      csx.put('\t');
      strcpy(buffer, "<diagram title=\"");
      if (((TConcreteScale*)((*concrete)[d]))->GetTitle() != "")
         strxmlcat(buffer, ((TConcreteScale*)((*concrete)[d]))->GetTitle().c_str());
      else
      	strxmlcat(buffer, ((TConcreteScale*)((*concrete)[d]))->GetName().c_str());
      strcat(buffer, "\">");
      csx.write(buffer, strlen(buffer));
      csx.put('\n');

      // write concepts

      const TQSDPointArray& points = cDiagram->GetPoints();
      uint p, pc = points.GetItemsInContainer();
      for (p = 0; p < pc; p++)
      {
      	// write header

      	csx.put('\t');
         csx.put('\t');
         strcpy(buffer, "<concept id=\"");
         ultoa(p + 1, buffer + strlen(buffer), 10);
         strcat(buffer, "\">");
         csx.write(buffer, strlen(buffer));
         csx.put('\n');

         // write position

      	csx.put('\t');
      	csx.put('\t');
      	csx.put('\t');
         sprintf(buffer, "<position x=\"%f\" y=\"%f\"/>",
         	points[p].GetXValue(), points[p].GetYValue());
         csx.write(buffer, strlen(buffer));
         csx.put('\n');

         // write objectContingent

      	csx.put('\t');
      	csx.put('\t');
      	csx.put('\t');
         strcpy(buffer, "<objectContingent>");
         csx.write(buffer, strlen(buffer));
         csx.put('\n');

         // first found object on a certain point positions the label

         bool label = false;

         const TQSObjectArray& objects = cDiagram->GetObjects();
         uint o, oc = objects.GetItemsInContainer();
         for (o = 0; o < oc; o++)
         {
         	if (objects[o].GetNumber() == int(p))
            {
            	if (label == false)
               {
               	// write lable style

                  TStringFormat format(objects[o].GetFormat());

               	// write header

                  csx.put('\t');
                  csx.put('\t');
                  csx.put('\t');
                  csx.put('\t');
                  strcpy(buffer, "<labelStyle>");
                  csx.write(buffer, strlen(buffer));
                  csx.put('\n');

                  // write offset

                  csx.put('\t');
                  csx.put('\t');
                  csx.put('\t');
                  csx.put('\t');
                  csx.put('\t');
                  sprintf(buffer, "<offset x=\"%f\" y=\"%f\"/>",
                  	double(format.the_x_offset), double(format.the_y_offset));
                  csx.write(buffer, strlen(buffer));
                  csx.put('\n');

                  // write text alignment

                  switch (format.the_h_pos)
                  {
                  	case 'l':
                     	strcpy(buffer, "\t\t\t\t\t<textAlignment>");
                     	strcat(buffer, "left");
                     	strcat(buffer, "</textAlignment>\n");
                        csx.write(buffer, strlen(buffer));
                        break;
                  	case 'c':
                     	strcpy(buffer, "\t\t\t\t\t<textAlignment>");
                     	strcat(buffer, "center");
                     	strcat(buffer, "</textAlignment>\n");
                        csx.write(buffer, strlen(buffer));
                        break;
                  	case 'r':
                     	strcpy(buffer, "\t\t\t\t\t<textAlignment>");
                     	strcat(buffer, "right");
                     	strcat(buffer, "</textAlignment>\n");
                        csx.write(buffer, strlen(buffer));
                        break;
                  }

               	// write footer

                  csx.put('\t');
                  csx.put('\t');
                  csx.put('\t');
                  csx.put('\t');
                  strcpy(buffer, "</labelStyle>");
                  csx.write(buffer, strlen(buffer));
                  csx.put('\n');

                  label = true;
               }

            	// write object

               csx.put('\t');
               csx.put('\t');
               csx.put('\t');
               csx.put('\t');
               strcpy(buffer, "<objectRef>");
               itoa(oid + int(o), buffer + strlen(buffer), 10);
               strcat(buffer, "</objectRef>");
               csx.write(buffer, strlen(buffer));
               csx.put('\n');
            }
         }

      	csx.put('\t');
      	csx.put('\t');
      	csx.put('\t');
         strcpy(buffer, "</objectContingent>");
         csx.write(buffer, strlen(buffer));
         csx.put('\n');

         // write attributeContingent

      	csx.put('\t');
      	csx.put('\t');
      	csx.put('\t');
         strcpy(buffer, "<attributeContingent>");
         csx.write(buffer, strlen(buffer));
         csx.put('\n');

         // first found attribute on a certain point positions the label

         label = false;

         const TQSAttributeArray& attributes = cDiagram->GetAttributes();
         uint a, ac = attributes.GetItemsInContainer();
         for (a = 0; a < ac; a++)
         {
         	if (attributes[a].GetNumber() == int(p))
            {
            	if (label == false)
               {
               	// write lable style

                  TStringFormat format(attributes[a].GetFormat());

               	// write header

                  csx.put('\t');
                  csx.put('\t');
                  csx.put('\t');
                  csx.put('\t');
                  strcpy(buffer, "<labelStyle>");
                  csx.write(buffer, strlen(buffer));
                  csx.put('\n');

                  // write offset

                  csx.put('\t');
                  csx.put('\t');
                  csx.put('\t');
                  csx.put('\t');
                  csx.put('\t');
                  sprintf(buffer, "<offset x=\"%f\" y=\"%f\"/>",
                  	double(format.the_x_offset), double(format.the_y_offset));
                  csx.write(buffer, strlen(buffer));
                  csx.put('\n');

                  // write text alignment

                  switch (format.the_h_pos)
                  {
                  	case 'l':
                     	strcpy(buffer, "\t\t\t\t\t<textAlignment>");
                     	strcat(buffer, "left");
                     	strcat(buffer, "</textAlignment>\n");
                        csx.write(buffer, strlen(buffer));
                        break;
                  	case 'c':
                     	strcpy(buffer, "\t\t\t\t\t<textAlignment>");
                     	strcat(buffer, "center");
                     	strcat(buffer, "</textAlignment>\n");
                        csx.write(buffer, strlen(buffer));
                        break;
                  	case 'r':
                     	strcpy(buffer, "\t\t\t\t\t<textAlignment>");
                     	strcat(buffer, "right");
                     	strcat(buffer, "</textAlignment>\n");
                        csx.write(buffer, strlen(buffer));
                        break;
                  }

               	// write footer

                  csx.put('\t');
                  csx.put('\t');
                  csx.put('\t');
                  csx.put('\t');
                  strcpy(buffer, "</labelStyle>");
                  csx.write(buffer, strlen(buffer));
                  csx.put('\n');

                  label = true;
               }

               // write attribute

               csx.put('\t');
               csx.put('\t');
               csx.put('\t');
               csx.put('\t');
               strcpy(buffer, "<attributeRef>");
               itoa(aid + int(a), buffer + strlen(buffer), 10);
               strcat(buffer, "</attributeRef>");
               csx.write(buffer, strlen(buffer));
               csx.put('\n');
            }
         }

      	csx.put('\t');
      	csx.put('\t');
      	csx.put('\t');
         strcpy(buffer, "</attributeContingent>");
         csx.write(buffer, strlen(buffer));
         csx.put('\n');

         // write footer

      	csx.put('\t');
         csx.put('\t');
         strcpy(buffer, "</concept>");
         csx.write(buffer, strlen(buffer));
         csx.put('\n');
      }

      // write edges

      const TQSDLineArray& lines = cDiagram->GetLines();
      uint l, lc = lines.GetItemsInContainer();
      for (l = 0; l < lc; l++)
      {
      	csx.put('\t');
         csx.put('\t');
         strcpy(buffer, "<edge from=\"");
         itoa(lines[l].GetFromPoint() + 1, buffer + strlen(buffer), 10);
         strcat(buffer, "\" to=\"");
         itoa(lines[l].GetToPoint() + 1, buffer + strlen(buffer), 10);
         strcat(buffer, "\"/>");
         csx.write(buffer, strlen(buffer));
         csx.put('\n');
      }

      // write diagram footer

      csx.put('\t');
      strcpy(buffer, "</diagram>");
      csx.write(buffer, strlen(buffer));
      csx.put('\n');

      oid += cDiagram->GetObjects().GetItemsInContainer();
      aid += cDiagram->GetAttributes().GetItemsInContainer();
   }

   // write schema footer

   strcpy(buffer, "</conceptualSchema>");
   csx.write(buffer, strlen(buffer));
   csx.put('\n');

   csx.close();

   delete scheme;
   delete concrete;
   delete csc;

   return error(PARSE_OK, source);
}

// -----------------------------------------------------------------------------
//
int parseDiagrams(int argc, char* argv[])
{
	// check whether module is part of the argument string
   // program only works with the initial name to ensure
   // program name retains intended meaning

   char *dest;
	int start = 0;
   if (argv[start][0] != '-')
   {
   	dest = strrchr(argv[start], '\\');
      if (dest == 0)
      	dest = argv[start];
      else
      	dest++;
      if ((stricmp(dest, "csc2csx.exe") == 0)
      	|| (stricmp(dest, "csc2csx") == 0))
         start++;
      else
      	return error(FILE_CORRUPT);
   }

	if (argc == start)
   	return error(WRONG_NUMBER_OF_ARGUMENTS);

   // check whether last argument contains the *.csx (xml) target name

   int stop = argc-1;
   char target[MAX_PATH] = "";
   char *source = 0;

   if (stop > start)
   {
   	if (argv[stop][0] == '-')
      	return error(NO_SOURCE_FILE, argv[stop]);

	   dest = strrchr(argv[stop], '.');
	   if (dest == 0)
	   {
      	if (argv[stop-1][0] != '-')
         {
         	// it is target name

            strcpy(target, argv[stop]);
            strcat(target, ".csx");
            stop--;
         }
	   }
	   else
	   {
	   	if (stricmp(dest, ".csx") == 0)
	      {
	      	strcpy(target, argv[stop]);
	         stop--;
	      }
	      else if (stricmp(dest, ".csc") == 0)
	      {
	         dest = strrchr(argv[stop], '\\');
	         if (dest != 0)
	         	strcpy(target, dest + 1);
	         else
	         	strcpy(target, argv[stop]);
	         dest = strrchr(target, '.');
	         strcpy(dest, ".csx");
	         source = argv[stop];
	         stop--;
	      }
         else
	      {
         	// other target types are accepted

	      	strcpy(target, argv[stop]);
	         stop--;
	      }
      }
   }

   // check whether second last argument contains the *.csc source name
   // only if source is not already found yet

   if (source == 0)
   {
   	if (argv[stop][0] == '-')
      	return error(NO_SOURCE_FILE, argv[stop]);

      dest = strrchr(argv[stop], '.');
      if (dest == 0)
      {
	   	// assume it is source name

         if (*target == 0)
         {
		      strcpy(target, argv[stop]);
		      strcat(target, ".csx");
         }

         source = target + strlen(target) + 1;
	      strcpy(source, argv[stop]);
	      strcat(source, ".csc");
         stop--;
	   }
      else
      {
        	if (stricmp(dest, ".csc") == 0)
         {
         	source = argv[stop];
            stop--;

            if (*target == 0)
            {
            	strcpy(target, argv[stop]);
               dest = strrchr(target, '.');
               strcpy(dest, ".csx");
            }
         }
         else
         	return error(INVALID_FILE_TYPE, argv[stop]);
      }
   }

   // check and set all options

   char *dsn = 0, *path = 0, *table = 0, *key = 0, *query = 0;
   char *program = 0, *stylefile = 0, *open = 0, *close = 0;
   char *schema = 0, *include = 0, *encoding = 0, *version = 0;
   int l = start, check = 0, msg = 0, askdb = 0, usedb = 0;

   while (l <= stop)
   {
   	if (strnicmp(argv[l], "-dd=", 4) == 0)
      	if (dsn != 0)
         	return error(OPTION_DEFINED, argv[l]);
         else if (path != 0)
         	return error(PATH_AND_DSN, argv[l]);
         else
         	dsn = argv[l] + 4;
   	if (stricmp(argv[l], "-dd") == 0)
      	if (dsn != 0)
         	return error(OPTION_DEFINED, argv[l]);
         else if (path != 0)
         	return error(PATH_AND_DSN, argv[l]);
         else
         	return error(MUST_TAKE_VALUE, argv[l]);
      else if (strnicmp(argv[l], "-dp=", 4) == 0)
      	if (path != 0)
         	return error(OPTION_DEFINED, argv[l]);
         else if (dsn != 0)
         	return error(PATH_AND_DSN, argv[l]);
         else
         	path = argv[l] + 4;
      else if (stricmp(argv[l], "-dp") == 0)
      	if (path != 0)
         	return error(OPTION_DEFINED, argv[l]);
         else if (dsn != 0)
         	return error(PATH_AND_DSN, argv[l]);
         else
         	return error(MUST_TAKE_VALUE, argv[l]);
      else if (strnicmp(argv[l], "-dt=", 4) == 0)
      	if (table != 0)
         	return error(OPTION_DEFINED, argv[l]);
         else if (query != 0)
         	return error(TABLE_KEY_AND_QUERY, argv[l]);
         else
         	table = argv[l] + 4;
      else if (stricmp(argv[l], "-dt") == 0)
      	if (table != 0)
         	return error(OPTION_DEFINED, argv[l]);
         else if (query != 0)
         	return error(TABLE_KEY_AND_QUERY, argv[l]);
         else
         	return error(MUST_TAKE_VALUE, argv[l]);
      else if (strnicmp(argv[l], "-dk=", 4) == 0)
      	if (key != 0)
         	return error(OPTION_DEFINED, argv[l]);
         else if (query != 0)
         	return error(TABLE_KEY_AND_QUERY, argv[l]);
         else
         	key = argv[l] + 4;
      else if (stricmp(argv[l], "-dk") == 0)
      	if (key != 0)
         	return error(OPTION_DEFINED, argv[l]);
         else if (query != 0)
         	return error(TABLE_KEY_AND_QUERY, argv[l]);
         else
         	return error(MUST_TAKE_VALUE, argv[l]);
      else if (strnicmp(argv[l], "-dq=", 4) == 0)
      	if (query != 0)
         	return error(OPTION_DEFINED, argv[l]);
         else if ((table != 0) || (key != 0))
         	return error(TABLE_KEY_AND_QUERY, argv[l]);
         else
         	query = argv[l] + 4;
      else if (stricmp(argv[l], "-dq") == 0)
      	if (query != 0)
         	return error(OPTION_DEFINED, argv[l]);
         else if ((table != 0) || (key != 0))
         	return error(TABLE_KEY_AND_QUERY, argv[l]);
         else
         	return error(MUST_TAKE_VALUE, argv[l]);
      else if (strnicmp(argv[l], "-vp=", 4) == 0)
      	if (program != 0)
         	return error(OPTION_DEFINED, argv[l]);
         else
         	program = argv[l] + 4;
      else if (stricmp(argv[l], "-vp") == 0)
      	if (program != 0)
         	return error(OPTION_DEFINED, argv[l]);
         else
         	return error(MUST_TAKE_VALUE, argv[l]);
      else if (strnicmp(argv[l], "-vs=", 4) == 0)
      	if (stylefile != 0)
         	return error(OPTION_DEFINED, argv[l]);
         else
         	stylefile = argv[l] + 4;
      else if (stricmp(argv[l], "-vs") == 0)
      	if (stylefile != 0)
         	return error(OPTION_DEFINED, argv[l]);
         else
         	return error(MUST_TAKE_VALUE, argv[l]);
      else if (strnicmp(argv[l], "-vo=", 4) == 0)
      	if (open != 0)
         	return error(OPTION_DEFINED, argv[l]);
         else
         	open = argv[l] + 4;
      else if (stricmp(argv[l], "-vo") == 0)
      	if (open != 0)
         	return error(OPTION_DEFINED, argv[l]);
         else
         	return error(MUST_TAKE_VALUE, argv[l]);
      else if (strnicmp(argv[l], "-vc=", 4) == 0)
      	if (close != 0)
         	return error(OPTION_DEFINED, argv[l]);
         else
         	close = argv[l] + 4;
      else if (stricmp(argv[l], "-vc") == 0)
      	if (close != 0)
         	return error(OPTION_DEFINED, argv[l]);
         else
         	return error(MUST_TAKE_VALUE, argv[l]);
      else if (stricmp(argv[l], "-db") == 0)
      	if (askdb != 0)
         	return error(OPTION_DEFINED, argv[l]);
         else
         	askdb = 1;
      else if (strnicmp(argv[l], "-db=", 4) == 0)
      	if (askdb != 0)
         	return error(OPTION_DEFINED, argv[l]);
         else
         	return error(DOES_NOT_TAKE_VALUE, argv[l]);
      else if (strnicmp(argv[l], "-xsi=", 5) == 0)
      	if (schema != 0)
         	return error(OPTION_DEFINED, argv[l]);
         else
         	schema = argv[l] + 5;
      else if (stricmp(argv[l], "-xsi") == 0)
      	if (schema != 0)
         	return error(OPTION_DEFINED, argv[l]);
         else
         	schema = schemaDefault;
      else if (strnicmp(argv[l], "-inc=", 5) == 0)
      	if (include != 0)
         	return error(OPTION_DEFINED, argv[l]);
         else
         	include = argv[l] + 5;
      else if (stricmp(argv[l], "-inc") == 0)
      	if (include != 0)
         	return error(OPTION_DEFINED, argv[l]);
         else
         	return error(MUST_TAKE_VALUE, argv[l]);
      else if (strnicmp(argv[l], "-enc=", 5) == 0)
      	if (encoding != 0)
         	return error(OPTION_DEFINED, argv[l]);
         else
         	encoding = argv[l] + 5;
      else if (stricmp(argv[l], "-enc") == 0)
      	if (encoding != 0)
         	return error(OPTION_DEFINED, argv[l]);
         else
         	encoding = encodingDefault;
      else if (strnicmp(argv[l], "-ver=", 5) == 0)
      	if (version != 0)
         	return error(OPTION_DEFINED, argv[l]);
         else
         	version = argv[l] + 5;
      else if (stricmp(argv[l], "-ver") == 0)
      	if (version != 0)
         	return error(OPTION_DEFINED, argv[l]);
         else
         	version = versionDefault;
      else if (stricmp(argv[l], "-c") == 0)
      	if (check != 0)
         	return error(OPTION_DEFINED, argv[l]);
         else
         	check = 1;
      else if (strnicmp(argv[l], "-c=", 3) == 0)
      	if (check != 0)
         	return error(OPTION_DEFINED, argv[l]);
         else
         	return error(DOES_NOT_TAKE_VALUE, argv[l]);
      else if (stricmp(argv[l], "-m") == 0)
      	if (msg != 0)
         	return error(OPTION_DEFINED, argv[l]);
         else
         	msg = 1;
      else if (strnicmp(argv[l], "-m=", 3) == 0)
      	if (msg != 0)
         	return error(OPTION_DEFINED, argv[l]);
         else
         	return error(DOES_NOT_TAKE_VALUE, argv[l]);
      else
      	return error(UNKNOWN_OPTION, argv[l]);

      l++;
   }

   // all options have been proceeded successfully
   // assume that combinations of options are valid

   // open source file (conscript *.csc) for read access

   TFCAFile* csc = new TFCAFile();
   if (include != 0)
   	csc->SetIncludePath(include);
   csc->SetName(source);
   int errOK = csc->ReadFile(check, msg, 0);
   if (errOK != ERR_OK)
   {
   	delete csc;
      return error(CANNOT_OPEN_FILE, source);
   }
   errOK = csc->ReadNewIncludeFiles(check, msg);
   if (errOK != ERR_OK)
   {
   	delete csc;
      return error(CANNOT_OPEN_INCLUDEFILE, source);
   }

   // check wheter csc file contains diagrams

	TIFCAArray* diagram = csc->GetListOfDiagrams();
   if ((diagram == 0) || (diagram->GetItemsInContainer() == 0))
   {
   	if (diagram != 0)
      	delete diagram;
   	delete csc;
      return error(SOURCE_IS_EMPTY, source);
   }

   // check whether csc file contains at most one (1!) conceptual scheme

	TIFCAArray* scheme = csc->GetListOfConceptualSchemes();
   if ((scheme == 0) || (scheme->GetItemsInContainer() > 1))
   {
   	if (scheme != 0)
      	delete scheme;
   	delete diagram;
   	delete csc;
      return error(SOURCE_IS_INCONSISTENT, source);
   }

   // check whether csc file contains database entry

   TDatabase* cscdb = 0;
   if (scheme->GetItemsInContainer() == 1)
   	cscdb = ((TConceptualScheme*)((*scheme)[0]))->GetDatabase();
   if ((dsn != 0) || (path != 0) || (table != 0) || (key != 0) || (query != 0)
      || ((cscdb != 0) && ((cscdb->GetDatabase() != "")
      || (cscdb->GetView() != "") || (cscdb->GetPrimaryKey() != ""))))
   {
      usedb = 1;
      askdb = 1;		// if yes, or any database entry was given, askDatabase becomes default
   }

   // csc file contains diagrams
   // open target file for write access

   ofstream csx(target);
   if (csx.bad())
   {
   	delete scheme;
   	delete diagram;
      delete csc;
   	return error(CANNOT_OPEN_FILE, target);
   }

   // proceed to parse csc to csx
   // write header -- use buffer for writing to file

   char buffer[1024];

   strcpy(buffer, "<?xml version=\"");
   if (version != 0)
   	strcat(buffer, version);
   else
   	strcat(buffer, versionDefault);
   strcat(buffer, "\" encoding=\"");
   if (encoding != 0)
   	strcat(buffer, encoding);
   else
   	strcat(buffer, encodingDefault);
   strcat(buffer, "\"?>");
   csx.write(buffer, strlen(buffer));
   csx.put('\n');

   // write schema header

   strcpy(buffer, "<conceptualSchema version=\"0.5\" askDatabase=\"");
   if (askdb != 0)
   	strcat(buffer, "true");
   else
   	strcat(buffer, "false");
   strcat(buffer, "\"");
   if (schema != 0)
   {
   	strcat(buffer, " xmlns:xsi=\"");
   	strcat(buffer, schemaInstance);
   	strcat(buffer, "\" xsi:noNamespaceSchemaLocation=\"");
      strcat(buffer, schema);
      strcat(buffer, "\"");
   }
   strcat(buffer, ">");
   csx.write(buffer, strlen(buffer));
   csx.put('\n');

   // write database (optional)

   if (usedb != 0)
   {
   	csx.put('\t');
      strcpy(buffer, "<database>");
      csx.write(buffer, strlen(buffer));
      csx.put('\n');

      if (dsn != 0)
      {
      	csx.put('\t');
         csx.put('\t');
         strcpy(buffer, "<dsn>");
         strcat(buffer, dsn);
         strcat(buffer, "</dsn>");
         csx.write(buffer, strlen(buffer));
         csx.put('\n');
      }
      else if ((cscdb->GetDatabase() != "") && (path == 0))
      {
      	csx.put('\t');
         csx.put('\t');
         strcpy(buffer, "<dsn>");
         strcat(buffer, cscdb->GetDatabase().c_str());
         strcat(buffer, "</dsn>");
         csx.write(buffer, strlen(buffer));
         csx.put('\n');
      }

      if (path != 0)
      {
      	csx.put('\t');
         csx.put('\t');
         strcpy(buffer, "<path>");
         strcat(buffer, path);
         strcat(buffer, "</path>");
         csx.write(buffer, strlen(buffer));
         csx.put('\n');
      }

      if (table != 0)
      {
      	csx.put('\t');
         csx.put('\t');
         strcpy(buffer, "<table>");
         strcat(buffer, table);
         strcat(buffer, "</table>");
         csx.write(buffer, strlen(buffer));
         csx.put('\n');
      }
      else if ((cscdb->GetView() != "") && (query == 0))
      {
      	csx.put('\t');
         csx.put('\t');
         strcpy(buffer, "<table>");
         strcat(buffer, cscdb->GetView().c_str());
         strcat(buffer, "</table>");
         csx.write(buffer, strlen(buffer));
         csx.put('\n');
      }

      if (key != 0)
      {
      	csx.put('\t');
         csx.put('\t');
         strcpy(buffer, "<key>");
         strcat(buffer, key);
         strcat(buffer, "</key>");
         csx.write(buffer, strlen(buffer));
         csx.put('\n');
      }
      else if ((cscdb->GetPrimaryKey() != "") && (query == 0))
      {
      	csx.put('\t');
         csx.put('\t');
         strcpy(buffer, "<key>");
         strcat(buffer, cscdb->GetPrimaryKey().c_str());
         strcat(buffer, "</key>");
         csx.write(buffer, strlen(buffer));
         csx.put('\n');
      }

      if (query != 0)
      {
      	csx.put('\t');
         csx.put('\t');
         strcpy(buffer, "<query>");
         strcat(buffer, query);
         strcat(buffer, "</query>");
         csx.write(buffer, strlen(buffer));
         csx.put('\n');
      }

   	csx.put('\t');
      strcpy(buffer, "</database>");
      csx.write(buffer, strlen(buffer));
      csx.put('\n');
   }

   // write viewer (optional)

   if ((program != 0) || (stylefile != 0) || (open != 0) || (close != 0))
   {
   	csx.put('\t');
      strcpy(buffer, "<viewer>");
      csx.write(buffer, strlen(buffer));
      csx.put('\n');

      if (program != 0)
      {
      	csx.put('\t');
         csx.put('\t');
         strcpy(buffer, "<program>");
         strcat(buffer, program);
         strcat(buffer, "</program>");
         csx.write(buffer, strlen(buffer));
         csx.put('\n');
      }

      if (stylefile != 0)
      {
      	csx.put('\t');
         csx.put('\t');
         strcpy(buffer, "<template>");
         strcat(buffer, stylefile);
         strcat(buffer, "</template>");
         csx.write(buffer, strlen(buffer));
         csx.put('\n');
      }

      if (open != 0)
      {
      	csx.put('\t');
         csx.put('\t');
         strcpy(buffer, "<openDelimiter>");
         strcat(buffer, open);
         strcat(buffer, "</openDelimiter>");
         csx.write(buffer, strlen(buffer));
         csx.put('\n');
      }

      if (close != 0)
      {
      	csx.put('\t');
         csx.put('\t');
         strcpy(buffer, "<closeDelimiter>");
         strcat(buffer, close);
         strcat(buffer, "</closeDelimiter>");
         csx.write(buffer, strlen(buffer));
         csx.put('\n');
      }

   	csx.put('\t');
      strcpy(buffer, "</viewer>");
      csx.write(buffer, strlen(buffer));
      csx.put('\n');
   }

   // write context (required)

   csx.put('\t');
   strcpy(buffer, "<context>");
   csx.write(buffer, strlen(buffer));
   csx.put('\n');

   // write objects

   uint id = 1;
   uint d, dc = diagram->GetItemsInContainer();
   TLineDiagram* cDiagram;
   for (d = 0; d < dc; d++)
   {
   	cDiagram = (TLineDiagram*)((*diagram)[d]);
      const TQSObjectArray& objects = cDiagram->GetObjects();
      uint o, oc = objects.GetItemsInContainer();
      for (o = 0; o < oc; o++)
      {
      	csx.put('\t');
         csx.put('\t');
         strcpy(buffer, "<object id=\"");
         itoa(id, buffer + strlen(buffer), 10);
         strcat(buffer, "\">");
         getObjectDescription(buffer + strlen(buffer), objects[o], csc, cDiagram, askdb);
         strcat(buffer, "</object>");
         csx.write(buffer, strlen(buffer));
         csx.put('\n');
         id++;
      }
   }

   // write attributes

   id = 1;
   for (d = 0; d < dc; d++)
   {
   	cDiagram = (TLineDiagram*)((*diagram)[d]);
      const TQSAttributeArray& attributes = cDiagram->GetAttributes();
      uint a, ac = attributes.GetItemsInContainer();
      for (a = 0; a < ac; a++)
      {
      	csx.put('\t');
         csx.put('\t');
         strcpy(buffer, "<attribute id=\"");
         itoa(id, buffer + strlen(buffer), 10);
         strcat(buffer, "\">");
         getAttributeDescription(buffer + strlen(buffer), attributes[a], csc, cDiagram);
         strcat(buffer, "</attribute>");
         csx.write(buffer, strlen(buffer));
         csx.put('\n');
         id++;
      }
   }

   csx.put('\t');
   strcpy(buffer, "</context>");
   csx.write(buffer, strlen(buffer));
   csx.put('\n');

   // write diagram (optional but given)

   int aid = 1, oid = 1;
   for (d = 0; d < dc; d++)
   {
   	cDiagram = (TLineDiagram*)((*diagram)[d]);

      // write diagram header

      csx.put('\t');
      strcpy(buffer, "<diagram title=\"");
      if (cDiagram->GetTitle() != "")
         strcat(buffer, cDiagram->GetTitle().c_str());
      else
      	strcat(buffer, cDiagram->GetName().c_str());
      strcat(buffer, "\">");
      csx.write(buffer, strlen(buffer));
      csx.put('\n');

      // write concepts

      const TQSDPointArray& points = cDiagram->GetPoints();
      uint p, pc = points.GetItemsInContainer();
      for (p = 0; p < pc; p++)
      {
      	// write header

      	csx.put('\t');
         csx.put('\t');
         strcpy(buffer, "<concept id=\"");
         ultoa(p + 1, buffer + strlen(buffer), 10);
         strcat(buffer, "\">");
         csx.write(buffer, strlen(buffer));
         csx.put('\n');

         // write position

      	csx.put('\t');
      	csx.put('\t');
      	csx.put('\t');
         sprintf(buffer, "<position x=\"%f\" y=\"%f\"/>",
         	points[p].GetXValue(), points[p].GetYValue());
         csx.write(buffer, strlen(buffer));
         csx.put('\n');

         // write objectContingent

      	csx.put('\t');
      	csx.put('\t');
      	csx.put('\t');
         strcpy(buffer, "<objectContingent>");
         csx.write(buffer, strlen(buffer));
         csx.put('\n');

         // first found object on a certain point positions the label

         bool label = false;

         const TQSObjectArray& objects = cDiagram->GetObjects();
         uint o, oc = objects.GetItemsInContainer();
         for (o = 0; o < oc; o++)
         {
         	if (objects[o].GetNumber() == int(p))
            {
            	if (label == false)
               {
               	// write lable style

                  TStringFormat format(objects[o].GetFormat());

               	// write header

                  csx.put('\t');
                  csx.put('\t');
                  csx.put('\t');
                  csx.put('\t');
                  strcpy(buffer, "<labelStyle>");
                  csx.write(buffer, strlen(buffer));
                  csx.put('\n');

                  // write offset

                  csx.put('\t');
                  csx.put('\t');
                  csx.put('\t');
                  csx.put('\t');
                  csx.put('\t');
                  sprintf(buffer, "<offset x=\"%f\" y=\"%f\"/>",
                  	double(format.the_x_offset), double(format.the_y_offset));
                  csx.write(buffer, strlen(buffer));
                  csx.put('\n');

                  // write text alignment

                  switch (format.the_h_pos)
                  {
                  	case 'l':
                     	strcpy(buffer, "\t\t\t\t\t<textAlignment>");
                     	strcat(buffer, "left");
                     	strcat(buffer, "</textAlignment>\n");
                        csx.write(buffer, strlen(buffer));
                        break;
                  	case 'c':
                     	strcpy(buffer, "\t\t\t\t\t<textAlignment>");
                     	strcat(buffer, "center");
                     	strcat(buffer, "</textAlignment>\n");
                        csx.write(buffer, strlen(buffer));
                        break;
                  	case 'r':
                     	strcpy(buffer, "\t\t\t\t\t<textAlignment>");
                     	strcat(buffer, "right");
                     	strcat(buffer, "</textAlignment>\n");
                        csx.write(buffer, strlen(buffer));
                        break;
                  }

               	// write footer

                  csx.put('\t');
                  csx.put('\t');
                  csx.put('\t');
                  csx.put('\t');
                  strcpy(buffer, "</labelStyle>");
                  csx.write(buffer, strlen(buffer));
                  csx.put('\n');

                  label = true;
               }

            	// write object

               csx.put('\t');
               csx.put('\t');
               csx.put('\t');
               csx.put('\t');
               strcpy(buffer, "<objectRef>");
               itoa(oid + int(o), buffer + strlen(buffer), 10);
               strcat(buffer, "</objectRef>");
               csx.write(buffer, strlen(buffer));
               csx.put('\n');
            }
         }

      	csx.put('\t');
      	csx.put('\t');
      	csx.put('\t');
         strcpy(buffer, "</objectContingent>");
         csx.write(buffer, strlen(buffer));
         csx.put('\n');

         // write attributeContingent

      	csx.put('\t');
      	csx.put('\t');
      	csx.put('\t');
         strcpy(buffer, "<attributeContingent>");
         csx.write(buffer, strlen(buffer));
         csx.put('\n');

         // first found attribute on a certain point positions the label

         label = false;

         const TQSAttributeArray& attributes = cDiagram->GetAttributes();
         uint a, ac = attributes.GetItemsInContainer();
         for (a = 0; a < ac; a++)
         {
         	if (attributes[a].GetNumber() == int(p))
            {
            	if (label == false)
               {
               	// write lable style

                  TStringFormat format(attributes[a].GetFormat());

               	// write header

                  csx.put('\t');
                  csx.put('\t');
                  csx.put('\t');
                  csx.put('\t');
                  strcpy(buffer, "<labelStyle>");
                  csx.write(buffer, strlen(buffer));
                  csx.put('\n');

                  // write offset

                  csx.put('\t');
                  csx.put('\t');
                  csx.put('\t');
                  csx.put('\t');
                  csx.put('\t');
                  sprintf(buffer, "<offset x=\"%f\" y=\"%f\"/>",
                  	double(format.the_x_offset), double(format.the_y_offset));
                  csx.write(buffer, strlen(buffer));
                  csx.put('\n');

                  // write text alignment

                  switch (format.the_h_pos)
                  {
                  	case 'l':
                     	strcpy(buffer, "\t\t\t\t\t<textAlignment>");
                     	strcat(buffer, "left");
                     	strcat(buffer, "</textAlignment>\n");
                        csx.write(buffer, strlen(buffer));
                        break;
                  	case 'c':
                     	strcpy(buffer, "\t\t\t\t\t<textAlignment>");
                     	strcat(buffer, "center");
                     	strcat(buffer, "</textAlignment>\n");
                        csx.write(buffer, strlen(buffer));
                        break;
                  	case 'r':
                     	strcpy(buffer, "\t\t\t\t\t<textAlignment>");
                     	strcat(buffer, "right");
                     	strcat(buffer, "</textAlignment>\n");
                        csx.write(buffer, strlen(buffer));
                        break;
                  }

               	// write footer

                  csx.put('\t');
                  csx.put('\t');
                  csx.put('\t');
                  csx.put('\t');
                  strcpy(buffer, "</labelStyle>");
                  csx.write(buffer, strlen(buffer));
                  csx.put('\n');

                  label = true;
               }

               // write attribute

               csx.put('\t');
               csx.put('\t');
               csx.put('\t');
               csx.put('\t');
               strcpy(buffer, "<attributeRef>");
               itoa(aid + int(a), buffer + strlen(buffer), 10);
               strcat(buffer, "</attributeRef>");
               csx.write(buffer, strlen(buffer));
               csx.put('\n');
            }
         }

      	csx.put('\t');
      	csx.put('\t');
      	csx.put('\t');
         strcpy(buffer, "</attributeContingent>");
         csx.write(buffer, strlen(buffer));
         csx.put('\n');

         // write footer

      	csx.put('\t');
         csx.put('\t');
         strcpy(buffer, "</concept>");
         csx.write(buffer, strlen(buffer));
         csx.put('\n');
      }

      // write edges

      const TQSDLineArray& lines = cDiagram->GetLines();
      uint l, lc = lines.GetItemsInContainer();
      for (l = 0; l < lc; l++)
      {
      	csx.put('\t');
         csx.put('\t');
         strcpy(buffer, "<edge from=\"");
         itoa(lines[l].GetFromPoint() + 1, buffer + strlen(buffer), 10);
         strcat(buffer, "\" to=\"");
         itoa(lines[l].GetToPoint() + 1, buffer + strlen(buffer), 10);
         strcat(buffer, "\"/>");
         csx.write(buffer, strlen(buffer));
         csx.put('\n');
      }

      // write diagram footer

      csx.put('\t');
      strcpy(buffer, "</diagram>");
      csx.write(buffer, strlen(buffer));
      csx.put('\n');

      oid += cDiagram->GetObjects().GetItemsInContainer();
      aid += cDiagram->GetAttributes().GetItemsInContainer();
   }

   // write schema footer

   strcpy(buffer, "</conceptualSchema>");
   csx.write(buffer, strlen(buffer));
   csx.put('\n');

   csx.close();

   delete scheme;
   delete diagram;
   delete csc;

   return error(PARSE_OK, source);
}

// -----------------------------------------------------------------------------
//
int error(uint errflag, char* path)
{
	switch (errflag)
   {
      case WRONG_NUMBER_OF_ARGUMENTS:
         break;
      case INVALID_FILE_NAME:
      	printf("\nERROR: %s is not a valid file name.\n", path);
         break;
      case INVALID_FILE_TYPE:
      	printf("\nERROR: %s is not of a valid type.\n", path);
         break;
      case NO_TARGET_FILE:
      	printf("\nERROR: No destination file name specified.\n");
         break;
      case NO_SOURCE_FILE:
      	printf("\nERROR: No source file name specified.\n");
         break;
      case UNKNOWN_OPTION:
      	printf("\nERROR: Option %s not known.\n", path);
         break;
      case PATH_AND_DSN:
      	printf("\nERROR: Not allowed to specify DSN entry and database path (%s).\n", path);
         break;
      case TABLE_KEY_AND_QUERY:
      	printf("\nERROR: Not allowed to specify table or key and query (%s).\n", path);
         break;
      case OPTION_DEFINED:
      	printf("\nERROR: Option %s has already been defined.\n", path);
         break;
      case DOES_NOT_TAKE_VALUE:
      	printf("\nERROR: Option %s does not take a value.\n", path);
         break;
      case MUST_TAKE_VALUE:
      	printf("\nERROR: Option %s must take a value.\n", path);
         break;
      case CANNOT_OPEN_FILE:
      	printf("\nERROR: Cannot open %s.\n", path);
         break;
      case SOURCE_IS_EMPTY:
      	printf("\nERROR: Source file %s does not contain any data.\n", path);
         break;
      case SOURCE_IS_INCONSISTENT:
      	printf("\nERROR: Source file %s is inconsistent.\n", path);
         break;
      case CANNOT_OPEN_INCLUDEFILE:
      	printf("\nERROR: Cannot open include file of %s.\n", path);
         break;
      case FILE_CORRUPT:
      	printf("\nWARNING: This file may be infected with a virus.\n");
         return 0;
      case PARSE_OK:
      	printf("\nOK: File %s has been parsed correctly.\n", path);
         return 0;
   }
   printf("\nCALL: csc2csx [-option[=value] ...] source[.csc] [target[.csx]]\n");
   printf("\t-ver=xml version number\n");
   printf("\t-enc=xml encoding\n");
   printf("\t-xsi[=csx schema] : use xml schema\n");
   printf("\t-inc=csc file include path\n");
   printf("\t-c : check consistency of csc file\n");
   printf("\t-m : show parsing messages of csc file\n");
   printf("\t-db : use database\n");
   printf("\t-dd=data source name\n");
   printf("\t-dp=database path\n");
   printf("\t-dt=table name\n");
   printf("\t-dk=primary key\n");
   printf("\t-dq=sql query\n");
   printf("\t-vp=external viewer\n");
   printf("\t-vt=template file\n");
   printf("\t-vo=open delimiter\n");
   printf("\t-vc=close delimiter\n");

   return 0;
}

// -----------------------------------------------------------------------------
//
int isWhere(const char* objName)
{
	if (objName[0] != 'O')
   	return 1;
   int len = strlen(objName);
   if (len > 4)
   	return 1;
   for (len--; len > 0; len--)
   	if ((objName[len] < '0') || (objName[len] > '9'))
      	return 1;
   return 0;
}

// -----------------------------------------------------------------------------
//
void getObjectDescription(char* desc, TObject& object, TFCAFile* cscFile,
	TLineDiagram* diagram, int generateQuery)
{
	*desc = 0;

	// find the abstract scales for the diagram

   TIFCAArray* abstract = cscFile->GetListOfAbstractScales();
   uint a, ac = abstract->GetItemsInContainer();
	TIFCAArray* concrete = cscFile->GetListOfConcreteScales();
   uint c, cc = concrete->GetItemsInContainer();
   TQueryMap* queryMap;

   for (a = 0; a < ac; a++)
   {
   	if (((TAbstractScale*)((*abstract)[a]))->GetDiagram(0) == diagram)
      {
      	for (c = 0; c < cc; c++)
         {
         	if (((TConcreteScale*)((*concrete)[c]))->GetAbstractScale() == ((TAbstractScale*)((*abstract)[a])))
            {
            	queryMap = ((TConcreteScale*)((*concrete)[c]))->GetQueryMap();

               if (queryMap != 0)
               {
               	const TstringArray& queries = queryMap->GetArguments();
               	const TstringArray& values = queryMap->GetValues();
                  int i = 0, m = values.GetItemsInContainer();

                  while ((i < m) && (values[i] != object.GetIdentifier()))
                  	i++;

                  if ((i < m) && (queries[i] != ""))
                  {
                  	if (*desc != 0)
                     	strcat(desc, "|");
                     strcat(desc, queries[i].c_str());
                  }
               }
            }
         }
      }
   }

   if (*desc == 0)
   {
   	if ((isWhere(object.GetDescription().c_str()) != 0)
      	|| (generateQuery == 0))
      	strcpy(desc, object.GetDescription().c_str());
      else
      {
      	TstringSet attribSet;
         diagram->GetIntent(object.GetNumber(), attribSet);
         const TQSAttributeArray& attributes = diagram->GetAttributes();
         uint s, sc = attributes.GetItemsInContainer();
         int lenWhere;
         for (s = 0; s < sc; s++)
         {
         	if (s > 0)
            	strcat(desc," AND ");
            if (attribSet.HasMember(attributes[s].GetIdentifier()))
            {
            	strcat(desc,"(");
               strcat(desc,"[");
               strcat(desc, attributes[s].GetDescription().c_str());
               strcat(desc,"]");
               lenWhere = strlen(desc);
               desc[lenWhere] = '=';
               desc[lenWhere + 1] = 39;
               desc[lenWhere + 2] = 'x';
               desc[lenWhere + 3] = 39;
               desc[lenWhere + 4] = '\0';
               strcat(desc," OR ");
               strcat(desc,"[");
               strcat(desc, attributes[s].GetDescription().c_str());
               strcat(desc,"]");
               lenWhere = strlen(desc);
               desc[lenWhere] = '=';
               desc[lenWhere + 1] = 39;
               desc[lenWhere + 2] = 'X';
               desc[lenWhere + 3] = 39;
               desc[lenWhere + 4] = '\0';
               strcat(desc,")");
            }
            else
            {
            	strcat(desc,"(");
               strcat(desc,"[");
               strcat(desc, attributes[s].GetDescription().c_str());
               strcat(desc,"]");
               lenWhere = strlen(desc);
               desc[lenWhere] = '=';
               desc[lenWhere + 1] = 39;
               desc[lenWhere + 2] = '.';
               desc[lenWhere + 3] = 39;
               desc[lenWhere + 4] = '\0';
               strcat(desc," OR ");
               strcat(desc,"(");
               strcat(desc,"[");
               strcat(desc, attributes[s].GetDescription().c_str());
               strcat(desc,"]");
               strcat(desc," IS NULL)");
               strcat(desc,")");
            }
         }
      }
   }

   delete abstract;
   delete concrete;
}

// -----------------------------------------------------------------------------
//
void getAttributeDescription(char* desc, TAttribute& attrib, TFCAFile* cscFile, TLineDiagram* diagram)
{
	*desc = 0;

	// find the abstract scales for the diagram

   TIFCAArray* abstract = cscFile->GetListOfAbstractScales();
   uint a, ac = abstract->GetItemsInContainer();
	TIFCAArray* concrete = cscFile->GetListOfConcreteScales();
   uint c, cc = concrete->GetItemsInContainer();
   TStringMap* stringMap;

   for (a = 0; a < ac; a++)
   {
   	if (((TAbstractScale*)((*abstract)[a]))->GetDiagram(0) == diagram)
      {
      	for (c = 0; c < cc; c++)
         {
         	if (((TConcreteScale*)((*concrete)[c]))->GetAbstractScale() == ((TAbstractScale*)((*abstract)[a])))
            {
            	stringMap = ((TConcreteScale*)((*concrete)[c]))->GetAttributeMap();

               if (stringMap != 0)
               {
               	const string& adesc = stringMap->GetValue(attrib.GetIdentifier());

                  if (adesc != "")
                  {
                  	if (*desc != 0)
                     	strcat(desc, "|");
                     strcat(desc, adesc.c_str());
                  }
               }
            }
         }
      }
   }

   if (*desc == 0)
   	strcpy(desc, attrib.GetDescription().c_str());

   delete abstract;
   delete concrete;
}

// -----------------------------------------------------------------------------
//
void getObjectDescription(char* desc, TObject& object, TConcreteScale* scale)
{
   TQueryMap* queryMap = scale->GetQueryMap();
   if (queryMap != 0)
   {
   	const TstringArray& queries = queryMap->GetArguments();
      const TstringArray& values = queryMap->GetValues();
      int i = 0, m = values.GetItemsInContainer();
      while ((i < m) && (values[i] != object.GetIdentifier()))
      	i++;
      if ((i < m) && (queries[i] != ""))
      	strxmlcpy(desc, queries[i].c_str());
   }
}

// -----------------------------------------------------------------------------
//
void getAttributeDescription(char* desc, TAttribute& attrib, TConcreteScale* scale)
{
   TStringMap* stringMap = scale->GetAttributeMap();
   if (stringMap != 0)
   {
   	const string& adesc = stringMap->GetValue(attrib.GetIdentifier());
      if (adesc != "")
      	strxmlcpy(desc, adesc.c_str());
   }
}

// -----------------------------------------------------------------------------
//
int checkConscriptFile(TFCAFile* csc, int generateQuery)
{
	int ok;
   ok = checkForUniqueDiagramInAbstractScales(csc);
   if (ok != ERR_OK)
   	return ok;
   ok = checkDiagramsInAbstractScales(csc);
   if (ok != ERR_OK)
   	return ok;
   ok = checkAbstractScalesInConcreteScales(csc, generateQuery);
   return ok;
}

// -----------------------------------------------------------------------------
//
int checkForUniqueDiagramInAbstractScales(TFCAFile* csc)
{
	TIFCAArray* abstract = csc->GetListOfAbstractScales();
   unsigned a = 0, ac = abstract->GetItemsInContainer();
   while ((a < ac) && (((TAbstractScale*)((*abstract)[a]))
     ->GetNumberOfDiagrams() == 1))
      a++;
   delete abstract;
   if (a < ac)
   	return ERR_OK + 1;
   return ERR_OK;
}

// -----------------------------------------------------------------------------
//
int checkDiagramsInAbstractScales(TFCAFile* csc)
{
	TIFCAArray* diagram = csc->GetListOfDiagrams();
	TIFCAArray* abstract = csc->GetListOfAbstractScales();
	TIFCAArray* context = csc->GetListOfContexts();
   TLineDiagram* cDiagram;
   TAbstractScale* cAbstract;
   TFormalContext* cContext;
   unsigned d, a, c, dc, ac, cc;

   // allow input of diagrams only
   dc = diagram->GetItemsInContainer();
   for (d = 0; d < dc; d++)
   {
   	cDiagram = (TLineDiagram*)((*diagram)[d]);

   	// try to find abstract scale for the diagram
      a = 0;
      ac = abstract->GetItemsInContainer();
      while ((a < ac) && (((TAbstractScale*)((*abstract)[a]))->GetDiagram(0)
        != cDiagram))
         a++;
      if (a == ac)
      {
      	// no abstract scale contains this diagram
         // try to find the context of the diagram
	      c = 0;
         cc = context->GetItemsInContainer();
	      while (c < cc)
	      {
		   	cContext = (TFormalContext*)((*context)[c]);
	         if (cContext->GetName() == cDiagram->GetName())
	         	break;
            c++;
	      }
	      if (c == cc)
	      {
	      	// context has not been found
            // create context from the diagram
            cContext = cDiagram->ComputeContext();
            csc->AddStructure(cContext);
         }
         else
         {
         	context->Detach(c);	// context is in use
         }

         // create abstract scale for diagram and context
         cAbstract = new TAbstractScale();
         cAbstract->SetName(cDiagram->GetName());
         if (cDiagram->GetTitle() != "")
         	cAbstract->SetTitle(cDiagram->GetTitle());
         else
         	cAbstract->SetTitle(cDiagram->GetName());
         cAbstract->SetContext(cContext);
         cAbstract->InsertDiagram(0, cDiagram);
         csc->AddStructure(cAbstract);
      }
      else
      {
      	abstract->Detach(a);		// abstract scale is in use
      }
   }

   delete diagram;
   delete context;

   if (abstract->GetItemsInContainer() > 0)
   {
   	delete abstract;
      return ERR_OK + 1;
   }

   delete abstract;
   return ERR_OK;
}

// -----------------------------------------------------------------------------
//
int checkAbstractScalesInConcreteScales(TFCAFile* csc, int generateQuery)
{
	TIFCAArray* concrete = csc->GetListOfConcreteScales();
	TIFCAArray* abstract = csc->GetListOfAbstractScales();
   TAbstractScale* cAbstract;
   TConcreteScale* cConcrete;
   TQueryMap* cQuery;
   TStringMap* cAttribute;
   unsigned c, a, q, s, ac, cc, qc, sc;
   ac = abstract->GetItemsInContainer();

   for (a = 0; a < ac; a++)
   {
   	cAbstract = (TAbstractScale*)((*abstract)[a]);

   	// try to find a concrete scale for abstract scale
      c = 0;
      cc = concrete->GetItemsInContainer();
      while ((c < cc) && (((TConcreteScale*)((*concrete)[c]))
        ->GetAbstractScale() != cAbstract))
        	c++;
      if (c < cc)
      {
      	// found a concrete scale for abstract scale
         // concrete scales based on this abstract scales are in use
      	concrete->Detach(c);
         cc--;
	      while (c < cc)
	      {
	         if (((TConcreteScale*)((*concrete)[c]))->GetAbstractScale()
	           == cAbstract)
            {
              	concrete->Detach(c);
               cc--;
            }
            else
            	c++;
         }
      }
      else
      {
      	// no concrete scale contains this abstract scale
         // create concrete scale for this abstract scale
         cConcrete = new TConcreteScale();
         cConcrete->SetName(cAbstract->GetName());
         if (cAbstract->GetTitle() != "")
         	cConcrete->SetTitle(cAbstract->GetTitle());
         else
         	cConcrete->SetTitle(cAbstract->GetName());
         csc->AddStructure(cConcrete);

         cQuery = new TQueryMap();
         cQuery->SetName(cAbstract->GetName());
         csc->AddStructure(cQuery);

         cAttribute = new TStringMap();
         cAttribute->SetName(cAbstract->GetName());
         csc->AddStructure(cAttribute);

         cConcrete->SetAbstractScale(cAbstract);
         cConcrete->SetQueryMap(cQuery);
         cConcrete->SetAttributeMap(cAttribute);

         TstringArray attribArray(10, 10);
         const TQSAttributeArray& attrb = cAbstract->GetDiagram(0)
         											->GetAttributes();
         sc = attrb.GetItemsInContainer();
         for (s = 0; s < sc; s++)
         {
         	cAttribute->AddArgumentAndValue(attrb[s].GetIdentifier(),
            											attrb[s].GetDescription());
            attribArray.Add(attrb[s].GetIdentifier());
         }

         TstringSet attribSet;
         char where[2048];
         int lenWhere;
         const TQSObjectArray& objct = cAbstract->GetDiagram(0)->GetObjects();
         qc = objct.GetItemsInContainer();
         for (q = 0; q < qc; q++)
         {
         	if ((isWhere(objct[q].GetDescription().c_str()) == 1)
              || (generateQuery == 0))
            {
            	// object name represents where clause
            	cQuery->AddArgumentAndValue(objct[q].GetDescription(),
            										objct[q].GetIdentifier());
            }
            else
            {
            	// object name does not represent where clause
               // create where clause from intent (attribute names)
               attribSet.Flush();
               cAbstract->GetDiagram(0)->GetIntent(objct[q].GetNumber(),
               	attribSet);
               where[0] = '\0';
               sc = attribArray.GetItemsInContainer();
               for (s = 0; s < sc; s++)
               {
               	if (s > 0)
                  	strcat(where," AND ");
               	if (attribSet.HasMember(attribArray[s]))
                  {
	                  strcat(where,"(");
	                  strcat(where,"[");
	                  strcat(where, cAttribute->GetValue(attribArray[s]).c_str());
	                  strcat(where,"]");
	                  lenWhere = strlen(where);
	                  where[lenWhere] = '=';
	                  where[lenWhere + 1] = 39;
                  	where[lenWhere + 2] = 'x';
	                  where[lenWhere + 3] = 39;
	                  where[lenWhere + 4] = '\0';
                     strcat(where," OR ");
	                  strcat(where,"[");
	                  strcat(where, cAttribute->GetValue(attribArray[s]).c_str());
	                  strcat(where,"]");
	                  lenWhere = strlen(where);
	                  where[lenWhere] = '=';
	                  where[lenWhere + 1] = 39;
                  	where[lenWhere + 2] = 'X';
	                  where[lenWhere + 3] = 39;
	                  where[lenWhere + 4] = '\0';
                     strcat(where,")");
                  }
                  else
                  {
	                  strcat(where,"(");
	                  strcat(where,"[");
	                  strcat(where, cAttribute->GetValue(attribArray[s]).c_str());
	                  strcat(where,"]");
	                  lenWhere = strlen(where);
	                  where[lenWhere] = '=';
	                  where[lenWhere + 1] = 39;
                  	where[lenWhere + 2] = '.';
	                  where[lenWhere + 3] = 39;
	                  where[lenWhere + 4] = '\0';
                     strcat(where," OR ");
	                  strcat(where,"(");
	                  strcat(where,"[");
	                  strcat(where, cAttribute->GetValue(attribArray[s]).c_str());
	                  strcat(where,"]");
                     strcat(where," IS NULL)");
                     strcat(where,")");
                  }
               }
            	cQuery->AddArgumentAndValue(where,
            										objct[q].GetIdentifier());
            }
         }
      }
   }

   delete abstract;

   if (concrete->GetItemsInContainer() > 0)
   {
   	delete concrete;
      return ERR_OK + 1;
   }

   delete concrete;
   return ERR_OK;
}

// -----------------------------------------------------------------------------
//
void strxml(char* xmlString)
{
   char buffer[1024];
   strcpy(buffer, xmlString);
   int i, n = strlen(buffer), j = 0;
   for (i = 0; i <= n; i++)
   {
   	switch(buffer[i])
      {
      	case '<':
         	strcpy(xmlString + j, "&lt;");
            j += 4;
            break;
         case '>':
         	strcpy(xmlString + j, "&gt;");
            j += 4;
            break;
         case '\'':
         	strcpy(xmlString + j, "&apos;");
            j += 6;
            break;
         case '"':
         	strcpy(xmlString + j, "&quot;");
            j += 6;
            break;
         case '&':
         	strcpy(xmlString + j, "&amp;");
            j += 5;
            break;
         default:
         	xmlString[j] = buffer[i];
            j++;
      }
   }
}

// -----------------------------------------------------------------------------
//
void strxmlcat(char* dest, const char* xmlString)
{
   int i, n = strlen(xmlString), j = strlen(dest);
   for (i = 0; i <= n; i++)
   {
   	switch(xmlString[i])
      {
      	case '<':
         	strcpy(dest + j, "&lt;");
            j += 4;
            break;
         case '>':
         	strcpy(dest + j, "&gt;");
            j += 4;
            break;
         case '\'':
         	strcpy(dest + j, "&apos;");
            j += 6;
            break;
         case '"':
         	strcpy(dest + j, "&quot;");
            j += 6;
            break;
         case '&':
         	strcpy(dest + j, "&amp;");
            j += 5;
            break;
         default:
         	dest[j] = xmlString[i];
            j++;
      }
   }
}

// -----------------------------------------------------------------------------
//
void strxmlcpy(char* dest, const char* xmlString)
{
   int i, n = strlen(xmlString), j = 0;
   for (i = 0; i <= n; i++)
   {
   	switch(xmlString[i])
      {
      	case '<':
         	strcpy(dest + j, "&lt;");
            j += 4;
            break;
         case '>':
         	strcpy(dest + j, "&gt;");
            j += 4;
            break;
         case '\'':
         	strcpy(dest + j, "&apos;");
            j += 6;
            break;
         case '"':
         	strcpy(dest + j, "&quot;");
            j += 6;
            break;
         case '&':
         	strcpy(dest + j, "&amp;");
            j += 5;
            break;
         default:
         	dest[j] = xmlString[i];
            j++;
      }
   }
}

