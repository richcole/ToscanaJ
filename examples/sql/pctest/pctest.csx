<?xml version="1.0" encoding="UTF-8"?>
<!--
This is an example conceptual schema for ToscanaJ.

It is meant to be used together with a database as defined by the SQL script
pctest.sql, which should have been provided along this file. With the setup
given this script will be loaded into the embedded database system.

If you want to use another DBMS, you have to change the definition from using
the <embed> element into something like:

   <url driver="sun.jdbc.odbc.JdbcOdbcDriver" user="user" password="secret">
            jdbc:odbc:PCTest80
   </url>
   
In this example the JDBC-ODBC bridge is used to connect to a data source named
"PCTest80" with the given username/password combination.

Refer to the documentation of your DBMS which class and URL scheme to use.
-->
<conceptualSchema version="TJ1.0">
  <description>
    <externalHTML url="pctest.html" />
  </description>
  <databaseConnection>
    <embed url="pctest.sql" />
    <table>PCTest</table>
    <key>PCname</key>
  </databaseConnection>
  <views>
    <objectView name="HTML View..." class="net.sourceforge.toscanaj.dbviewer.HTMLDatabaseViewer">
      <template>
        <html>
          <body>
            <h1>
              <field content="&quot;PCname&quot;" />
            </h1>
            <table border="1" width="100%">
              <tr>
                <th colspan="2">Features</th>
              </tr>
              <tr>
                <td>Type of case:</td>
                <td>
                  <field content="&quot;typeCase&quot;" />
                </td>
              </tr>
              <tr>
                <td>Size of harddisk:</td>
                <td>
                  <field content="&quot;harddisk&quot;" />
                </td>
              </tr>
              <tr>
                <td>Bus system:</td>
                <td>
                  <field content="&quot;typeBus&quot;" />
                </td>
              </tr>
              <tr>
                <td>Software:</td>
                <td>
                  <field content="&quot;software&quot;" />
                </td>
              </tr>
            </table>
            <div align="right">
              <font size="+1">
                <b>
                  Price: 
                  <field content="&quot;price&quot;" />
                </b>
              </font>
            </div>
          </body>
        </html>
      </template>
    </objectView>
    <objectView name="HTML View (Color)..." class="net.sourceforge.toscanaj.dbviewer.HTMLDatabaseViewer">
      <template url="views/coloredSummary.html" />
    </objectView>
    <objectView name="Short View..." class="net.sourceforge.toscanaj.dbviewer.SimpleDatabaseViewer">
      <template url="views/simple.txt" />
      <parameter name="openDelimiter" value="$" />
      <parameter name="closeDelimiter" value="$" />
    </objectView>
    <objectListView name="HTML Report..." class="net.sourceforge.toscanaj.dbviewer.HTMLDatabaseViewer">
      <template url="views/listView.html" />
    </objectListView>
    <objectListView name="Benchmark View..." class="net.sourceforge.toscanaj.dbviewer.BarChartDatabaseViewer">
      <template>
        <column sqlname="&quot;dosmark&quot;" displayname="Dos Benchmark" mincolor="#00ffff" maxcolor="#ff00ff" linecolor="#ff0000" />
        <column sqlname="&quot;diskmark&quot;" displayname="Disk Benchmark" mincolor="#00ffff" maxcolor="#ff00ff" linecolor="#ff0000" />
        <column sqlname="&quot;price&quot;" displayname="Price" mincolor="#ff0000" maxcolor="#ffff00" linecolor="#ffffff" />
      </template>
    </objectListView>
    <objectListView name="Video/Graphics View..." class="net.sourceforge.toscanaj.dbviewer.BarChartDatabaseViewer">
      <template>
        <column sqlname="&quot;video&quot;" displayname="Video" mincolor="#00ffff" maxcolor="#ff00ff" linecolor="#ff0000" />
        <column sqlname="&quot;graphics&quot;" displayname="Graphics" mincolor="#00ffff" maxcolor="#ff00ff" linecolor="#ff0000" />
        <column sqlname="&quot;price&quot;" displayname="Price" mincolor="#ff0000" maxcolor="#ffff00" linecolor="#ffffff" />
      </template>
    </objectListView>
    <!-- This one is just showing syntax, it does not do anything useful. It would if the program would be found
         and there would be a table AttributeURLs, mapping AttributeNames to DescriptionURLs.
         since we don't have those, we don't actually use the setting. -->
    <!--
    <attributeView class="net.sourceforge.toscanaj.dbviewer.ProgramCallDatabaseViewer" name="Open description...">
        <parameter name="openDelimiter" value="$$$"/>
        <parameter name="closeDelimiter" value="%%%"/>
        <parameter name="commandLine" value="/some/path/to/mozilla/bin/mozilla $$$DescriptionURL%%%"/>
        <table>AttributeURLs</table>
        <key>AttributeName</key>
    </attributeView>
    -->
  </views>
  <queries>
    <distinctListQuery name="Cases">
      <queryField>&quot;typeCase&quot;</queryField>
    </distinctListQuery>
    <listQuery name="Name with type of case" head="Case: ">
      <queryField name="Case" separator=" - PC: ">&quot;typeCase&quot;</queryField>
      <queryField name="Name">&quot;PCname&quot;</queryField>
    </listQuery>
    <aggregateQuery name="Average Price">
      <queryField format="$ 0.00">AVG(&quot;price&quot;)</queryField>
    </aggregateQuery>
    <aggregateQuery name="Average Price (relative)">
      <queryField format="$ 0.00" separator=" (">AVG(&quot;price&quot;)</queryField>
      <queryField format="0.00 %" relative="true" separator=")">AVG(&quot;price&quot;)</queryField>
    </aggregateQuery>
    <aggregateQuery name="Min/Max Prices">
      <queryField separator=" - " format="$0.00">MIN(&quot;price&quot;)</queryField>
      <queryField format="$0.00">MAX(&quot;price&quot;)</queryField>
    </aggregateQuery>
    <aggregateQuery name="Counts (relative)">
      <queryField separator=" (">count(*)</queryField>
      <queryField format="0.00 %" relative="true" separator=")">count(*)</queryField>
    </aggregateQuery>
  </queries>
  <diagram title="Prices for 486/66 PCs">
    <description>
      <html>
        <body>
          <h1>Prices</h1>
          
                    This diagrams gives a general overview on the price distribution
                    in the PC set.
                
        </body>
      </html>
    </description>
    <node id="1">
      <position x="0.0" y="-0.0" />
      <attributeLabelStyle>
        <offset x="-3.0" y="-3.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>right</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent />
        <attributeContingent />
      </concept>
    </node>
    <node id="2">
      <position x="20.0" y="20.0" />
      <attributeLabelStyle>
        <offset x="3.0" y="-3.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent />
        <attributeContingent>
          <attribute>&gt;=2500$</attribute>
        </attributeContingent>
      </concept>
    </node>
    <node id="3">
      <position x="40.0" y="40.0" />
      <attributeLabelStyle>
        <offset x="3.0" y="-3.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent />
        <attributeContingent>
          <attribute>&gt;=3000$</attribute>
        </attributeContingent>
      </concept>
    </node>
    <node id="4">
      <position x="60.0" y="60.0" />
      <attributeLabelStyle>
        <offset x="3.0" y="-3.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent />
        <attributeContingent>
          <attribute>&gt;=3500$</attribute>
        </attributeContingent>
      </concept>
    </node>
    <node id="5">
      <position x="80.0" y="80.0" />
      <attributeLabelStyle>
        <offset x="3.0" y="-3.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent />
        <attributeContingent>
          <attribute>&gt;=4000$</attribute>
        </attributeContingent>
      </concept>
    </node>
    <node id="6">
      <position x="100.0" y="100.0" />
      <attributeLabelStyle>
        <offset x="3.0" y="-3.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent />
        <attributeContingent>
          <attribute>&gt;=4500$</attribute>
        </attributeContingent>
      </concept>
    </node>
    <node id="7">
      <position x="120.0" y="120.0" />
      <attributeLabelStyle>
        <offset x="3.0" y="-3.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="4.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;price&quot;&gt;=5000</object>
        </objectContingent>
        <attributeContingent>
          <attribute>&gt;=5000$</attribute>
        </attributeContingent>
      </concept>
    </node>
    <node id="8">
      <position x="-20.0" y="20.0" />
      <attributeLabelStyle>
        <offset x="-3.0" y="-3.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>right</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent />
        <attributeContingent>
          <attribute>&lt;5000$</attribute>
        </attributeContingent>
      </concept>
    </node>
    <node id="9">
      <position x="0.0" y="40.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent />
        <attributeContingent />
      </concept>
    </node>
    <node id="10">
      <position x="20.0" y="60.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent />
        <attributeContingent />
      </concept>
    </node>
    <node id="11">
      <position x="40.0" y="80.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent />
        <attributeContingent />
      </concept>
    </node>
    <node id="12">
      <position x="60.0" y="100.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent />
        <attributeContingent />
      </concept>
    </node>
    <node id="13">
      <position x="80.0" y="120.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="4.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;price&quot;&lt;5000 and &quot;price&quot;&gt;=4500</object>
        </objectContingent>
        <attributeContingent />
      </concept>
    </node>
    <node id="14">
      <position x="-40.0" y="40.0" />
      <attributeLabelStyle>
        <offset x="-3.0" y="-3.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>right</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent />
        <attributeContingent>
          <attribute>&lt;4500$</attribute>
        </attributeContingent>
      </concept>
    </node>
    <node id="15">
      <position x="-20.0" y="60.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent />
        <attributeContingent />
      </concept>
    </node>
    <node id="16">
      <position x="0.0" y="80.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent />
        <attributeContingent />
      </concept>
    </node>
    <node id="17">
      <position x="20.0" y="100.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent />
        <attributeContingent />
      </concept>
    </node>
    <node id="18">
      <position x="40.0" y="120.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="4.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;price&quot;&lt;4500 and &quot;price&quot;&gt;=4000</object>
        </objectContingent>
        <attributeContingent />
      </concept>
    </node>
    <node id="19">
      <position x="-60.0" y="60.0" />
      <attributeLabelStyle>
        <offset x="-3.0" y="-3.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>right</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent />
        <attributeContingent>
          <attribute>&lt;4000$</attribute>
        </attributeContingent>
      </concept>
    </node>
    <node id="20">
      <position x="-40.0" y="80.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent />
        <attributeContingent />
      </concept>
    </node>
    <node id="21">
      <position x="-20.0" y="100.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent />
        <attributeContingent />
      </concept>
    </node>
    <node id="22">
      <position x="0.0" y="120.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="4.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;price&quot;&lt;4000 and &quot;price&quot;&gt;=3500</object>
        </objectContingent>
        <attributeContingent />
      </concept>
    </node>
    <node id="23">
      <position x="-80.0" y="80.0" />
      <attributeLabelStyle>
        <offset x="-3.0" y="-3.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>right</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent />
        <attributeContingent>
          <attribute>&lt;3500$</attribute>
        </attributeContingent>
      </concept>
    </node>
    <node id="24">
      <position x="-60.0" y="100.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent />
        <attributeContingent />
      </concept>
    </node>
    <node id="25">
      <position x="-40.0" y="120.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="4.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;price&quot;&lt;3500 and &quot;price&quot;&gt;=3000</object>
        </objectContingent>
        <attributeContingent />
      </concept>
    </node>
    <node id="26">
      <position x="-100.0" y="100.0" />
      <attributeLabelStyle>
        <offset x="-3.0" y="-3.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>right</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent />
        <attributeContingent>
          <attribute>&lt;3000$</attribute>
        </attributeContingent>
      </concept>
    </node>
    <node id="27">
      <position x="-80.0" y="120.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="4.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;price&quot;&lt;3000 and &quot;price&quot;&gt;=2500</object>
        </objectContingent>
        <attributeContingent />
      </concept>
    </node>
    <node id="28">
      <position x="-120.0" y="120.0" />
      <attributeLabelStyle>
        <offset x="-3.0" y="-3.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>right</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="4.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;price&quot;&lt;2500</object>
        </objectContingent>
        <attributeContingent>
          <attribute>&lt;2500$</attribute>
        </attributeContingent>
      </concept>
    </node>
    <node id="29">
      <position x="0.0" y="160.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent />
        <attributeContingent />
      </concept>
    </node>
    <edge from="1" to="2" />
    <edge from="1" to="8" />
    <edge from="2" to="3" />
    <edge from="2" to="9" />
    <edge from="3" to="4" />
    <edge from="3" to="10" />
    <edge from="4" to="5" />
    <edge from="4" to="11" />
    <edge from="5" to="6" />
    <edge from="5" to="12" />
    <edge from="6" to="7" />
    <edge from="6" to="13" />
    <edge from="7" to="29" />
    <edge from="8" to="9" />
    <edge from="8" to="14" />
    <edge from="9" to="10" />
    <edge from="9" to="15" />
    <edge from="10" to="11" />
    <edge from="10" to="16" />
    <edge from="11" to="12" />
    <edge from="11" to="17" />
    <edge from="12" to="13" />
    <edge from="12" to="18" />
    <edge from="13" to="29" />
    <edge from="14" to="15" />
    <edge from="14" to="19" />
    <edge from="15" to="16" />
    <edge from="15" to="20" />
    <edge from="16" to="17" />
    <edge from="16" to="21" />
    <edge from="17" to="18" />
    <edge from="17" to="22" />
    <edge from="18" to="29" />
    <edge from="19" to="20" />
    <edge from="19" to="23" />
    <edge from="20" to="21" />
    <edge from="20" to="24" />
    <edge from="21" to="22" />
    <edge from="21" to="25" />
    <edge from="22" to="29" />
    <edge from="23" to="24" />
    <edge from="23" to="26" />
    <edge from="24" to="25" />
    <edge from="24" to="27" />
    <edge from="25" to="29" />
    <edge from="26" to="27" />
    <edge from="26" to="28" />
    <edge from="27" to="29" />
    <edge from="28" to="29" />
  </diagram>
  <diagram title="Sizes of harddisks of 486/66 PCs">
    <node id="1">
      <position x="0.0" y="-0.0" />
      <attributeLabelStyle>
        <offset x="-3.0" y="-3.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>right</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent />
        <attributeContingent />
      </concept>
    </node>
    <node id="2">
      <position x="20.0" y="20.0" />
      <attributeLabelStyle>
        <offset x="3.0" y="-3.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent />
        <attributeContingent>
          <attribute>&gt;=225MB</attribute>
        </attributeContingent>
      </concept>
    </node>
    <node id="3">
      <position x="40.0" y="40.0" />
      <attributeLabelStyle>
        <offset x="3.0" y="-3.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent />
        <attributeContingent>
          <attribute>&gt;=250MB</attribute>
        </attributeContingent>
      </concept>
    </node>
    <node id="4">
      <position x="60.0" y="60.0" />
      <attributeLabelStyle>
        <offset x="3.0" y="-3.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent />
        <attributeContingent>
          <attribute>&gt;=350MB</attribute>
        </attributeContingent>
      </concept>
    </node>
    <node id="5">
      <position x="80.0" y="80.0" />
      <attributeLabelStyle>
        <offset x="3.0" y="-3.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent />
        <attributeContingent>
          <attribute>&gt;=400MB</attribute>
        </attributeContingent>
      </concept>
    </node>
    <node id="6">
      <position x="100.0" y="100.0" />
      <attributeLabelStyle>
        <offset x="3.0" y="-3.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent />
        <attributeContingent>
          <attribute>&gt;=450MB</attribute>
        </attributeContingent>
      </concept>
    </node>
    <node id="7">
      <position x="120.0" y="120.0" />
      <attributeLabelStyle>
        <offset x="3.0" y="-3.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="4.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;harddisk&quot;&gt;=500</object>
        </objectContingent>
        <attributeContingent>
          <attribute>&gt;=500MB</attribute>
        </attributeContingent>
      </concept>
    </node>
    <node id="8">
      <position x="-20.0" y="20.0" />
      <attributeLabelStyle>
        <offset x="-3.0" y="-3.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>right</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent />
        <attributeContingent>
          <attribute>&lt;500MB</attribute>
        </attributeContingent>
      </concept>
    </node>
    <node id="9">
      <position x="0.0" y="40.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent />
        <attributeContingent />
      </concept>
    </node>
    <node id="10">
      <position x="20.0" y="60.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent />
        <attributeContingent />
      </concept>
    </node>
    <node id="11">
      <position x="40.0" y="80.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent />
        <attributeContingent />
      </concept>
    </node>
    <node id="12">
      <position x="60.0" y="100.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent />
        <attributeContingent />
      </concept>
    </node>
    <node id="13">
      <position x="80.0" y="120.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="4.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;harddisk&quot;&lt;500 and &quot;harddisk&quot;&gt;=450</object>
        </objectContingent>
        <attributeContingent />
      </concept>
    </node>
    <node id="14">
      <position x="-40.0" y="40.0" />
      <attributeLabelStyle>
        <offset x="-3.0" y="-3.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>right</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent />
        <attributeContingent>
          <attribute>&lt;450MB</attribute>
        </attributeContingent>
      </concept>
    </node>
    <node id="15">
      <position x="-20.0" y="60.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent />
        <attributeContingent />
      </concept>
    </node>
    <node id="16">
      <position x="0.0" y="80.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent />
        <attributeContingent />
      </concept>
    </node>
    <node id="17">
      <position x="20.0" y="100.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent />
        <attributeContingent />
      </concept>
    </node>
    <node id="18">
      <position x="40.0" y="120.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="4.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;harddisk&quot;&lt;450 and &quot;harddisk&quot;&gt;=400</object>
        </objectContingent>
        <attributeContingent />
      </concept>
    </node>
    <node id="19">
      <position x="-60.0" y="60.0" />
      <attributeLabelStyle>
        <offset x="-3.0" y="-3.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>right</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent />
        <attributeContingent>
          <attribute>&lt;400MB</attribute>
        </attributeContingent>
      </concept>
    </node>
    <node id="20">
      <position x="-40.0" y="80.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent />
        <attributeContingent />
      </concept>
    </node>
    <node id="21">
      <position x="-20.0" y="100.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent />
        <attributeContingent />
      </concept>
    </node>
    <node id="22">
      <position x="0.0" y="120.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="4.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;harddisk&quot;&lt;400 and &quot;harddisk&quot;&gt;=350</object>
        </objectContingent>
        <attributeContingent />
      </concept>
    </node>
    <node id="23">
      <position x="-80.0" y="80.0" />
      <attributeLabelStyle>
        <offset x="-3.0" y="-3.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>right</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent />
        <attributeContingent>
          <attribute>&lt;350MB</attribute>
        </attributeContingent>
      </concept>
    </node>
    <node id="24">
      <position x="-60.0" y="100.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent />
        <attributeContingent />
      </concept>
    </node>
    <node id="25">
      <position x="-40.0" y="120.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="4.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;harddisk&quot;&lt;350 and &quot;harddisk&quot;&gt;=300</object>
        </objectContingent>
        <attributeContingent />
      </concept>
    </node>
    <node id="26">
      <position x="-100.0" y="100.0" />
      <attributeLabelStyle>
        <offset x="-3.0" y="-3.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>right</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent />
        <attributeContingent>
          <attribute>&lt;250MB</attribute>
        </attributeContingent>
      </concept>
    </node>
    <node id="27">
      <position x="-80.0" y="120.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="4.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;harddisk&quot;&lt;250 and &quot;harddisk&quot;&gt;=225</object>
        </objectContingent>
        <attributeContingent />
      </concept>
    </node>
    <node id="28">
      <position x="-120.0" y="120.0" />
      <attributeLabelStyle>
        <offset x="-3.0" y="-3.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>right</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="4.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;harddisk&quot;&lt;225</object>
        </objectContingent>
        <attributeContingent>
          <attribute>&lt;225MB</attribute>
        </attributeContingent>
      </concept>
    </node>
    <node id="29">
      <position x="0.0" y="160.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent />
        <attributeContingent />
      </concept>
    </node>
    <edge from="1" to="2" />
    <edge from="1" to="8" />
    <edge from="2" to="3" />
    <edge from="2" to="9" />
    <edge from="3" to="4" />
    <edge from="3" to="10" />
    <edge from="4" to="5" />
    <edge from="4" to="11" />
    <edge from="5" to="6" />
    <edge from="5" to="12" />
    <edge from="6" to="7" />
    <edge from="6" to="13" />
    <edge from="7" to="29" />
    <edge from="8" to="9" />
    <edge from="8" to="14" />
    <edge from="9" to="10" />
    <edge from="9" to="15" />
    <edge from="10" to="11" />
    <edge from="10" to="16" />
    <edge from="11" to="12" />
    <edge from="11" to="17" />
    <edge from="12" to="13" />
    <edge from="12" to="18" />
    <edge from="13" to="29" />
    <edge from="14" to="15" />
    <edge from="14" to="19" />
    <edge from="15" to="16" />
    <edge from="15" to="20" />
    <edge from="16" to="17" />
    <edge from="16" to="21" />
    <edge from="17" to="18" />
    <edge from="17" to="22" />
    <edge from="18" to="29" />
    <edge from="19" to="20" />
    <edge from="19" to="23" />
    <edge from="20" to="21" />
    <edge from="20" to="24" />
    <edge from="21" to="22" />
    <edge from="21" to="25" />
    <edge from="22" to="29" />
    <edge from="23" to="24" />
    <edge from="23" to="26" />
    <edge from="24" to="25" />
    <edge from="24" to="27" />
    <edge from="25" to="29" />
    <edge from="26" to="27" />
    <edge from="26" to="28" />
    <edge from="27" to="29" />
    <edge from="28" to="29" />
  </diagram>
  <diagram title="WinMarks (graphics/disk) of 486/66 PCs">
    <node id="1">
      <position x="0.0" y="-0.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-20.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="20.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;diskmark&quot;&gt;0 and &quot;diskmark&quot;&lt;=15 AND &quot;graphics&quot;&gt;0 and &quot;graphics&quot;&lt;=5</object>
        </objectContingent>
        <attributeContingent>
          <attribute>Disk WinMark &gt; 0, Graphics WinMark &gt; 0</attribute>
        </attributeContingent>
      </concept>
    </node>
    <node id="2">
      <position x="40.0" y="40.0" />
      <attributeLabelStyle>
        <offset x="20.0" y="-20.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="20.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;diskmark&quot;&gt;0 and &quot;diskmark&quot;&lt;=15 AND &quot;graphics&quot;&gt;5 and &quot;graphics&quot;&lt;=10</object>
        </objectContingent>
        <attributeContingent>
          <attribute>&gt; 5</attribute>
        </attributeContingent>
      </concept>
    </node>
    <node id="3">
      <position x="80.0" y="80.0" />
      <attributeLabelStyle>
        <offset x="20.0" y="-20.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="20.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;diskmark&quot;&gt;0 and &quot;diskmark&quot;&lt;=15 AND &quot;graphics&quot;&gt;10 and &quot;graphics&quot;&lt;=15</object>
        </objectContingent>
        <attributeContingent>
          <attribute>&gt; 10</attribute>
        </attributeContingent>
      </concept>
    </node>
    <node id="4">
      <position x="120.0" y="120.0" />
      <attributeLabelStyle>
        <offset x="20.0" y="-20.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="20.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;diskmark&quot;&gt;0 and &quot;diskmark&quot;&lt;=15 AND &quot;graphics&quot;&gt;15 and &quot;graphics&quot;&lt;=20</object>
        </objectContingent>
        <attributeContingent>
          <attribute>&gt; 15</attribute>
        </attributeContingent>
      </concept>
    </node>
    <node id="5">
      <position x="160.0" y="160.0" />
      <attributeLabelStyle>
        <offset x="20.0" y="-20.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="20.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;diskmark&quot;&gt;0 and &quot;diskmark&quot;&lt;=15 AND &quot;graphics&quot;&gt;20 and &quot;graphics&quot;&lt;=30</object>
        </objectContingent>
        <attributeContingent>
          <attribute>&gt; 20</attribute>
        </attributeContingent>
      </concept>
    </node>
    <node id="6">
      <position x="200.0" y="200.0" />
      <attributeLabelStyle>
        <offset x="20.0" y="-20.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="20.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;diskmark&quot;&gt;0 and &quot;diskmark&quot;&lt;=15 AND &quot;graphics&quot;&gt;30 and &quot;graphics&quot;&lt;=40</object>
        </objectContingent>
        <attributeContingent>
          <attribute>&gt; 30</attribute>
        </attributeContingent>
      </concept>
    </node>
    <node id="7">
      <position x="240.0" y="240.0" />
      <attributeLabelStyle>
        <offset x="20.0" y="-20.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="20.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;diskmark&quot;&gt;0 and &quot;diskmark&quot;&lt;=15 AND &quot;graphics&quot;&gt;40</object>
        </objectContingent>
        <attributeContingent>
          <attribute>&gt; 40</attribute>
        </attributeContingent>
      </concept>
    </node>
    <node id="8">
      <position x="-40.0" y="40.0" />
      <attributeLabelStyle>
        <offset x="-20.0" y="-20.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>right</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="20.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;diskmark&quot;&gt;15 and &quot;diskmark&quot;&lt;=30 AND &quot;graphics&quot;&gt;0 and &quot;graphics&quot;&lt;=5</object>
        </objectContingent>
        <attributeContingent>
          <attribute>&gt; 15</attribute>
        </attributeContingent>
      </concept>
    </node>
    <node id="9">
      <position x="-80.0" y="80.0" />
      <attributeLabelStyle>
        <offset x="-20.0" y="-20.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>right</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="20.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;diskmark&quot;&gt;30 and &quot;diskmark&quot;&lt;=45 AND &quot;graphics&quot;&gt;0 and &quot;graphics&quot;&lt;=5</object>
        </objectContingent>
        <attributeContingent>
          <attribute>&gt; 30</attribute>
        </attributeContingent>
      </concept>
    </node>
    <node id="10">
      <position x="-120.0" y="120.0" />
      <attributeLabelStyle>
        <offset x="-20.0" y="-20.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>right</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="20.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;diskmark&quot;&gt;45 and &quot;diskmark&quot;&lt;=60 AND &quot;graphics&quot;&gt;0 and &quot;graphics&quot;&lt;=5</object>
        </objectContingent>
        <attributeContingent>
          <attribute>&gt; 45</attribute>
        </attributeContingent>
      </concept>
    </node>
    <node id="11">
      <position x="-160.0" y="160.0" />
      <attributeLabelStyle>
        <offset x="-20.0" y="-20.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>right</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="20.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;diskmark&quot;&gt;60 and &quot;diskmark&quot;&lt;=90 AND &quot;graphics&quot;&gt;0 and &quot;graphics&quot;&lt;=5</object>
        </objectContingent>
        <attributeContingent>
          <attribute>&gt; 60</attribute>
        </attributeContingent>
      </concept>
    </node>
    <node id="12">
      <position x="-200.0" y="200.0" />
      <attributeLabelStyle>
        <offset x="-20.0" y="-20.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>right</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="20.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;diskmark&quot;&gt;90 and &quot;diskmark&quot;&lt;=120 AND &quot;graphics&quot;&gt;0 and &quot;graphics&quot;&lt;=5</object>
        </objectContingent>
        <attributeContingent>
          <attribute>&gt; 90</attribute>
        </attributeContingent>
      </concept>
    </node>
    <node id="13">
      <position x="-240.0" y="240.0" />
      <attributeLabelStyle>
        <offset x="-20.0" y="-20.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>right</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="20.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;diskmark&quot;&gt;120 AND &quot;graphics&quot;&gt;0 and &quot;graphics&quot;&lt;=5</object>
        </objectContingent>
        <attributeContingent>
          <attribute>&gt; 120</attribute>
        </attributeContingent>
      </concept>
    </node>
    <node id="14">
      <position x="0.0" y="80.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="20.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;diskmark&quot;&gt;15 and &quot;diskmark&quot;&lt;=30 AND &quot;graphics&quot;&gt;5 and &quot;graphics&quot;&lt;=10</object>
        </objectContingent>
        <attributeContingent />
      </concept>
    </node>
    <node id="15">
      <position x="40.0" y="120.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="20.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;diskmark&quot;&gt;15 and &quot;diskmark&quot;&lt;=30 AND &quot;graphics&quot;&gt;10 and &quot;graphics&quot;&lt;=15</object>
        </objectContingent>
        <attributeContingent />
      </concept>
    </node>
    <node id="16">
      <position x="80.0" y="160.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="20.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;diskmark&quot;&gt;15 and &quot;diskmark&quot;&lt;=30 AND &quot;graphics&quot;&gt;15 and &quot;graphics&quot;&lt;=20</object>
        </objectContingent>
        <attributeContingent />
      </concept>
    </node>
    <node id="17">
      <position x="120.0" y="200.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="20.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;diskmark&quot;&gt;15 and &quot;diskmark&quot;&lt;=30 AND &quot;graphics&quot;&gt;20 and &quot;graphics&quot;&lt;=30</object>
        </objectContingent>
        <attributeContingent />
      </concept>
    </node>
    <node id="18">
      <position x="160.0" y="240.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="20.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;diskmark&quot;&gt;15 and &quot;diskmark&quot;&lt;=30 AND &quot;graphics&quot;&gt;30 and &quot;graphics&quot;&lt;=40</object>
        </objectContingent>
        <attributeContingent />
      </concept>
    </node>
    <node id="19">
      <position x="200.0" y="280.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="20.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;diskmark&quot;&gt;15 and &quot;diskmark&quot;&lt;=30 AND &quot;graphics&quot;&gt;40</object>
        </objectContingent>
        <attributeContingent />
      </concept>
    </node>
    <node id="20">
      <position x="-40.0" y="120.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="20.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;diskmark&quot;&gt;30 and &quot;diskmark&quot;&lt;=45 AND &quot;graphics&quot;&gt;5 and &quot;graphics&quot;&lt;=10</object>
        </objectContingent>
        <attributeContingent />
      </concept>
    </node>
    <node id="21">
      <position x="-80.0" y="160.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="20.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;diskmark&quot;&gt;45 and &quot;diskmark&quot;&lt;=60 AND &quot;graphics&quot;&gt;5 and &quot;graphics&quot;&lt;=10</object>
        </objectContingent>
        <attributeContingent />
      </concept>
    </node>
    <node id="22">
      <position x="-120.0" y="200.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="20.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;diskmark&quot;&gt;60 and &quot;diskmark&quot;&lt;=90 AND &quot;graphics&quot;&gt;5 and &quot;graphics&quot;&lt;=10</object>
        </objectContingent>
        <attributeContingent />
      </concept>
    </node>
    <node id="23">
      <position x="-160.0" y="240.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="20.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;diskmark&quot;&gt;90 and &quot;diskmark&quot;&lt;=120 AND &quot;graphics&quot;&gt;5 and &quot;graphics&quot;&lt;=10</object>
        </objectContingent>
        <attributeContingent />
      </concept>
    </node>
    <node id="24">
      <position x="-200.0" y="280.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="20.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;diskmark&quot;&gt;120 AND &quot;graphics&quot;&gt;5 and &quot;graphics&quot;&lt;=10</object>
        </objectContingent>
        <attributeContingent />
      </concept>
    </node>
    <node id="25">
      <position x="0.0" y="160.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="20.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;diskmark&quot;&gt;30 and &quot;diskmark&quot;&lt;=45 AND &quot;graphics&quot;&gt;10 and &quot;graphics&quot;&lt;=15</object>
        </objectContingent>
        <attributeContingent />
      </concept>
    </node>
    <node id="26">
      <position x="40.0" y="200.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="20.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;diskmark&quot;&gt;30 and &quot;diskmark&quot;&lt;=45 AND &quot;graphics&quot;&gt;15 and &quot;graphics&quot;&lt;=20</object>
        </objectContingent>
        <attributeContingent />
      </concept>
    </node>
    <node id="27">
      <position x="80.0" y="240.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="20.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;diskmark&quot;&gt;30 and &quot;diskmark&quot;&lt;=45 AND &quot;graphics&quot;&gt;20 and &quot;graphics&quot;&lt;=30</object>
        </objectContingent>
        <attributeContingent />
      </concept>
    </node>
    <node id="28">
      <position x="120.0" y="280.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="20.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;diskmark&quot;&gt;30 and &quot;diskmark&quot;&lt;=45 AND &quot;graphics&quot;&gt;30 and &quot;graphics&quot;&lt;=40</object>
        </objectContingent>
        <attributeContingent />
      </concept>
    </node>
    <node id="29">
      <position x="160.0" y="320.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="20.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;diskmark&quot;&gt;30 and &quot;diskmark&quot;&lt;=45 AND &quot;graphics&quot;&gt;40</object>
        </objectContingent>
        <attributeContingent />
      </concept>
    </node>
    <node id="30">
      <position x="-40.0" y="200.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="20.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;diskmark&quot;&gt;45 and &quot;diskmark&quot;&lt;=60 AND &quot;graphics&quot;&gt;10 and &quot;graphics&quot;&lt;=15</object>
        </objectContingent>
        <attributeContingent />
      </concept>
    </node>
    <node id="31">
      <position x="-80.0" y="240.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="20.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;diskmark&quot;&gt;60 and &quot;diskmark&quot;&lt;=90 AND &quot;graphics&quot;&gt;10 and &quot;graphics&quot;&lt;=15</object>
        </objectContingent>
        <attributeContingent />
      </concept>
    </node>
    <node id="32">
      <position x="-120.0" y="280.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="20.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;diskmark&quot;&gt;90 and &quot;diskmark&quot;&lt;=120 AND &quot;graphics&quot;&gt;10 and &quot;graphics&quot;&lt;=15</object>
        </objectContingent>
        <attributeContingent />
      </concept>
    </node>
    <node id="33">
      <position x="-160.0" y="320.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="20.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;diskmark&quot;&gt;120 AND &quot;graphics&quot;&gt;10 and &quot;graphics&quot;&lt;=15</object>
        </objectContingent>
        <attributeContingent />
      </concept>
    </node>
    <node id="34">
      <position x="0.0" y="240.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="20.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;diskmark&quot;&gt;45 and &quot;diskmark&quot;&lt;=60 AND &quot;graphics&quot;&gt;15 and &quot;graphics&quot;&lt;=20</object>
        </objectContingent>
        <attributeContingent />
      </concept>
    </node>
    <node id="35">
      <position x="40.0" y="280.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="20.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;diskmark&quot;&gt;45 and &quot;diskmark&quot;&lt;=60 AND &quot;graphics&quot;&gt;20 and &quot;graphics&quot;&lt;=30</object>
        </objectContingent>
        <attributeContingent />
      </concept>
    </node>
    <node id="36">
      <position x="80.0" y="320.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="20.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;diskmark&quot;&gt;45 and &quot;diskmark&quot;&lt;=60 AND &quot;graphics&quot;&gt;30 and &quot;graphics&quot;&lt;=40</object>
        </objectContingent>
        <attributeContingent />
      </concept>
    </node>
    <node id="37">
      <position x="120.0" y="360.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="20.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;diskmark&quot;&gt;45 and &quot;diskmark&quot;&lt;=60 AND &quot;graphics&quot;&gt;40</object>
        </objectContingent>
        <attributeContingent />
      </concept>
    </node>
    <node id="38">
      <position x="-40.0" y="280.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="20.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;diskmark&quot;&gt;60 and &quot;diskmark&quot;&lt;=90 AND &quot;graphics&quot;&gt;15 and &quot;graphics&quot;&lt;=20</object>
        </objectContingent>
        <attributeContingent />
      </concept>
    </node>
    <node id="39">
      <position x="-80.0" y="320.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="20.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;diskmark&quot;&gt;90 and &quot;diskmark&quot;&lt;=120 AND &quot;graphics&quot;&gt;15 and &quot;graphics&quot;&lt;=20</object>
        </objectContingent>
        <attributeContingent />
      </concept>
    </node>
    <node id="40">
      <position x="-120.0" y="360.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="20.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;diskmark&quot;&gt;120 AND &quot;graphics&quot;&gt;15 and &quot;graphics&quot;&lt;=20</object>
        </objectContingent>
        <attributeContingent />
      </concept>
    </node>
    <node id="41">
      <position x="0.0" y="320.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="20.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;diskmark&quot;&gt;60 and &quot;diskmark&quot;&lt;=90 AND &quot;graphics&quot;&gt;20 and &quot;graphics&quot;&lt;=30</object>
        </objectContingent>
        <attributeContingent />
      </concept>
    </node>
    <node id="42">
      <position x="40.0" y="360.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="20.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;diskmark&quot;&gt;60 and &quot;diskmark&quot;&lt;=90 AND &quot;graphics&quot;&gt;30 and &quot;graphics&quot;&lt;=40</object>
        </objectContingent>
        <attributeContingent />
      </concept>
    </node>
    <node id="43">
      <position x="80.0" y="400.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="20.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;diskmark&quot;&gt;60 and &quot;diskmark&quot;&lt;=90 AND &quot;graphics&quot;&gt;40</object>
        </objectContingent>
        <attributeContingent />
      </concept>
    </node>
    <node id="44">
      <position x="-40.0" y="360.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="20.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;diskmark&quot;&gt;90 and &quot;diskmark&quot;&lt;=120 AND &quot;graphics&quot;&gt;20 and &quot;graphics&quot;&lt;=30</object>
        </objectContingent>
        <attributeContingent />
      </concept>
    </node>
    <node id="45">
      <position x="-80.0" y="400.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="20.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;diskmark&quot;&gt;120 AND &quot;graphics&quot;&gt;20 and &quot;graphics&quot;&lt;=30</object>
        </objectContingent>
        <attributeContingent />
      </concept>
    </node>
    <node id="46">
      <position x="0.0" y="400.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="20.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;diskmark&quot;&gt;90 and &quot;diskmark&quot;&lt;=120 AND &quot;graphics&quot;&gt;30 and &quot;graphics&quot;&lt;=40</object>
        </objectContingent>
        <attributeContingent />
      </concept>
    </node>
    <node id="47">
      <position x="40.0" y="440.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="20.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;diskmark&quot;&gt;90 and &quot;diskmark&quot;&lt;=120 AND &quot;graphics&quot;&gt;40</object>
        </objectContingent>
        <attributeContingent />
      </concept>
    </node>
    <node id="48">
      <position x="-40.0" y="440.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="20.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;diskmark&quot;&gt;120 AND &quot;graphics&quot;&gt;30 and &quot;graphics&quot;&lt;=40</object>
        </objectContingent>
        <attributeContingent />
      </concept>
    </node>
    <node id="49">
      <position x="0.0" y="480.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="20.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;diskmark&quot;&gt;120 AND &quot;graphics&quot;&gt;40</object>
        </objectContingent>
        <attributeContingent />
      </concept>
    </node>
    <edge from="1" to="2" />
    <edge from="1" to="8" />
    <edge from="2" to="3" />
    <edge from="2" to="14" />
    <edge from="3" to="4" />
    <edge from="3" to="15" />
    <edge from="4" to="5" />
    <edge from="4" to="16" />
    <edge from="5" to="6" />
    <edge from="5" to="17" />
    <edge from="6" to="7" />
    <edge from="6" to="18" />
    <edge from="7" to="19" />
    <edge from="8" to="9" />
    <edge from="8" to="14" />
    <edge from="9" to="10" />
    <edge from="9" to="20" />
    <edge from="10" to="11" />
    <edge from="10" to="21" />
    <edge from="11" to="12" />
    <edge from="11" to="22" />
    <edge from="12" to="13" />
    <edge from="12" to="23" />
    <edge from="13" to="24" />
    <edge from="14" to="15" />
    <edge from="14" to="20" />
    <edge from="15" to="16" />
    <edge from="15" to="25" />
    <edge from="16" to="17" />
    <edge from="16" to="26" />
    <edge from="17" to="18" />
    <edge from="17" to="27" />
    <edge from="18" to="19" />
    <edge from="18" to="28" />
    <edge from="19" to="29" />
    <edge from="20" to="21" />
    <edge from="20" to="25" />
    <edge from="21" to="22" />
    <edge from="21" to="30" />
    <edge from="22" to="23" />
    <edge from="22" to="31" />
    <edge from="23" to="24" />
    <edge from="23" to="32" />
    <edge from="24" to="33" />
    <edge from="25" to="26" />
    <edge from="25" to="30" />
    <edge from="26" to="27" />
    <edge from="26" to="34" />
    <edge from="27" to="28" />
    <edge from="27" to="35" />
    <edge from="28" to="29" />
    <edge from="28" to="36" />
    <edge from="29" to="37" />
    <edge from="30" to="31" />
    <edge from="30" to="34" />
    <edge from="31" to="32" />
    <edge from="31" to="38" />
    <edge from="32" to="33" />
    <edge from="32" to="39" />
    <edge from="33" to="40" />
    <edge from="34" to="35" />
    <edge from="34" to="38" />
    <edge from="35" to="36" />
    <edge from="35" to="41" />
    <edge from="36" to="37" />
    <edge from="36" to="42" />
    <edge from="37" to="43" />
    <edge from="38" to="39" />
    <edge from="38" to="41" />
    <edge from="39" to="40" />
    <edge from="39" to="44" />
    <edge from="40" to="45" />
    <edge from="41" to="42" />
    <edge from="41" to="44" />
    <edge from="42" to="43" />
    <edge from="42" to="46" />
    <edge from="43" to="47" />
    <edge from="44" to="45" />
    <edge from="44" to="46" />
    <edge from="45" to="48" />
    <edge from="46" to="47" />
    <edge from="46" to="48" />
    <edge from="47" to="49" />
    <edge from="48" to="49" />
  </diagram>
  <diagram title="Types of bus systems of the 486/66 PCs">
    <node id="1">
      <position x="0.0" y="-0.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="20.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent />
        <attributeContingent />
      </concept>
    </node>
    <node id="2">
      <position x="80.0" y="80.0" />
      <attributeLabelStyle>
        <offset x="12.0" y="-12.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="12.0" y="12.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;typeBus&quot;='ISA'</object>
        </objectContingent>
        <attributeContingent>
          <attribute>ISA-Bus</attribute>
        </attributeContingent>
      </concept>
    </node>
    <node id="3">
      <position x="80.0" y="160.0" />
      <attributeLabelStyle>
        <offset x="12.0" y="-12.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="12.0" y="12.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;typeBus&quot;='EISA'</object>
        </objectContingent>
        <attributeContingent>
          <attribute>EISA-Bus</attribute>
        </attributeContingent>
      </concept>
    </node>
    <node id="4">
      <position x="-80.0" y="120.0" />
      <attributeLabelStyle>
        <offset x="-12.0" y="-12.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>right</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="-12.0" y="12.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>right</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;typeBus&quot;='MCA'</object>
        </objectContingent>
        <attributeContingent>
          <attribute>MCA-Bus</attribute>
        </attributeContingent>
      </concept>
    </node>
    <node id="5">
      <position x="0.0" y="240.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent />
        <attributeContingent />
      </concept>
    </node>
    <edge from="1" to="2" />
    <edge from="1" to="4" />
    <edge from="2" to="3" />
    <edge from="3" to="5" />
    <edge from="4" to="5" />
  </diagram>
  <diagram title="Sizes of harddisks (ordinal)">
    <node id="1">
      <position x="0.0" y="-0.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="15.0" y="15.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;harddisk&quot; &lt; 200</object>
        </objectContingent>
        <attributeContingent />
      </concept>
    </node>
    <node id="2">
      <position x="0.0" y="100.0" />
      <attributeLabelStyle>
        <offset x="15.0" y="-15.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="15.0" y="15.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;harddisk&quot; Between 200 And 399</object>
        </objectContingent>
        <attributeContingent>
          <attribute>&gt;=200MB</attribute>
        </attributeContingent>
      </concept>
    </node>
    <node id="3">
      <position x="0.0" y="200.0" />
      <attributeLabelStyle>
        <offset x="15.0" y="-15.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="15.0" y="15.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;harddisk&quot; &gt;= 400</object>
        </objectContingent>
        <attributeContingent>
          <attribute>&gt;=400MB</attribute>
        </attributeContingent>
      </concept>
    </node>
    <edge from="1" to="2" />
    <edge from="2" to="3" />
  </diagram>
  <diagram title="Video throughput (1000 operations per second)">
    <node id="1">
      <position x="0.0" y="-0.0" />
      <attributeLabelStyle>
        <offset x="-3.0" y="-3.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>right</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent />
        <attributeContingent />
      </concept>
    </node>
    <node id="2">
      <position x="20.0" y="20.0" />
      <attributeLabelStyle>
        <offset x="3.0" y="-3.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent />
        <attributeContingent>
          <attribute>&gt;=1500</attribute>
        </attributeContingent>
      </concept>
    </node>
    <node id="3">
      <position x="40.0" y="40.0" />
      <attributeLabelStyle>
        <offset x="3.0" y="-3.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent />
        <attributeContingent>
          <attribute>&gt;=3000</attribute>
        </attributeContingent>
      </concept>
    </node>
    <node id="4">
      <position x="60.0" y="60.0" />
      <attributeLabelStyle>
        <offset x="3.0" y="-3.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent />
        <attributeContingent>
          <attribute>&gt;=4500</attribute>
        </attributeContingent>
      </concept>
    </node>
    <node id="5">
      <position x="80.0" y="80.0" />
      <attributeLabelStyle>
        <offset x="3.0" y="-3.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent />
        <attributeContingent>
          <attribute>&gt;=6000</attribute>
        </attributeContingent>
      </concept>
    </node>
    <node id="6">
      <position x="100.0" y="100.0" />
      <attributeLabelStyle>
        <offset x="3.0" y="-3.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent />
        <attributeContingent>
          <attribute>&gt;=7500</attribute>
        </attributeContingent>
      </concept>
    </node>
    <node id="7">
      <position x="120.0" y="120.0" />
      <attributeLabelStyle>
        <offset x="3.0" y="-3.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="4.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;video&quot;&gt;=9000</object>
        </objectContingent>
        <attributeContingent>
          <attribute>&gt;=9000</attribute>
        </attributeContingent>
      </concept>
    </node>
    <node id="8">
      <position x="-20.0" y="20.0" />
      <attributeLabelStyle>
        <offset x="-3.0" y="-3.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>right</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent />
        <attributeContingent>
          <attribute>&lt;9000</attribute>
        </attributeContingent>
      </concept>
    </node>
    <node id="9">
      <position x="0.0" y="40.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent />
        <attributeContingent />
      </concept>
    </node>
    <node id="10">
      <position x="20.0" y="60.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent />
        <attributeContingent />
      </concept>
    </node>
    <node id="11">
      <position x="40.0" y="80.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent />
        <attributeContingent />
      </concept>
    </node>
    <node id="12">
      <position x="60.0" y="100.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent />
        <attributeContingent />
      </concept>
    </node>
    <node id="13">
      <position x="80.0" y="120.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="4.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;video&quot; between 7500 and 8999</object>
        </objectContingent>
        <attributeContingent />
      </concept>
    </node>
    <node id="14">
      <position x="-40.0" y="40.0" />
      <attributeLabelStyle>
        <offset x="-3.0" y="-3.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>right</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent />
        <attributeContingent>
          <attribute>&lt;7500</attribute>
        </attributeContingent>
      </concept>
    </node>
    <node id="15">
      <position x="-20.0" y="60.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent />
        <attributeContingent />
      </concept>
    </node>
    <node id="16">
      <position x="0.0" y="80.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent />
        <attributeContingent />
      </concept>
    </node>
    <node id="17">
      <position x="20.0" y="100.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent />
        <attributeContingent />
      </concept>
    </node>
    <node id="18">
      <position x="40.0" y="120.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="4.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;video&quot; between 6000 and 7499</object>
        </objectContingent>
        <attributeContingent />
      </concept>
    </node>
    <node id="19">
      <position x="-60.0" y="60.0" />
      <attributeLabelStyle>
        <offset x="-3.0" y="-3.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>right</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent />
        <attributeContingent>
          <attribute>&lt;6000</attribute>
        </attributeContingent>
      </concept>
    </node>
    <node id="20">
      <position x="-40.0" y="80.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent />
        <attributeContingent />
      </concept>
    </node>
    <node id="21">
      <position x="-20.0" y="100.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent />
        <attributeContingent />
      </concept>
    </node>
    <node id="22">
      <position x="0.0" y="120.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="4.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;video&quot; between 4500 and 5999</object>
        </objectContingent>
        <attributeContingent />
      </concept>
    </node>
    <node id="23">
      <position x="-80.0" y="80.0" />
      <attributeLabelStyle>
        <offset x="-3.0" y="-3.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>right</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent />
        <attributeContingent>
          <attribute>&lt;4500</attribute>
        </attributeContingent>
      </concept>
    </node>
    <node id="24">
      <position x="-60.0" y="100.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent />
        <attributeContingent />
      </concept>
    </node>
    <node id="25">
      <position x="-40.0" y="120.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="4.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;video&quot; between 3000 and 4499</object>
        </objectContingent>
        <attributeContingent />
      </concept>
    </node>
    <node id="26">
      <position x="-100.0" y="100.0" />
      <attributeLabelStyle>
        <offset x="-3.0" y="-3.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>right</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent />
        <attributeContingent>
          <attribute>&lt;3000</attribute>
        </attributeContingent>
      </concept>
    </node>
    <node id="27">
      <position x="-80.0" y="120.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="4.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;video&quot; between 1500 and 2999</object>
        </objectContingent>
        <attributeContingent />
      </concept>
    </node>
    <node id="28">
      <position x="-120.0" y="120.0" />
      <attributeLabelStyle>
        <offset x="-3.0" y="-3.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>right</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="4.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;video&quot;&lt;1500</object>
        </objectContingent>
        <attributeContingent>
          <attribute>&lt;1500</attribute>
        </attributeContingent>
      </concept>
    </node>
    <node id="29">
      <position x="0.0" y="160.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent />
        <attributeContingent />
      </concept>
    </node>
    <edge from="1" to="2" />
    <edge from="1" to="8" />
    <edge from="2" to="3" />
    <edge from="2" to="9" />
    <edge from="3" to="4" />
    <edge from="3" to="10" />
    <edge from="4" to="5" />
    <edge from="4" to="11" />
    <edge from="5" to="6" />
    <edge from="5" to="12" />
    <edge from="6" to="7" />
    <edge from="6" to="13" />
    <edge from="7" to="29" />
    <edge from="8" to="9" />
    <edge from="8" to="14" />
    <edge from="9" to="10" />
    <edge from="9" to="15" />
    <edge from="10" to="11" />
    <edge from="10" to="16" />
    <edge from="11" to="12" />
    <edge from="11" to="17" />
    <edge from="12" to="13" />
    <edge from="12" to="18" />
    <edge from="13" to="29" />
    <edge from="14" to="15" />
    <edge from="14" to="19" />
    <edge from="15" to="16" />
    <edge from="15" to="20" />
    <edge from="16" to="17" />
    <edge from="16" to="21" />
    <edge from="17" to="18" />
    <edge from="17" to="22" />
    <edge from="18" to="29" />
    <edge from="19" to="20" />
    <edge from="19" to="23" />
    <edge from="20" to="21" />
    <edge from="20" to="24" />
    <edge from="21" to="22" />
    <edge from="21" to="25" />
    <edge from="22" to="29" />
    <edge from="23" to="24" />
    <edge from="23" to="26" />
    <edge from="24" to="25" />
    <edge from="24" to="27" />
    <edge from="25" to="29" />
    <edge from="26" to="27" />
    <edge from="26" to="28" />
    <edge from="27" to="29" />
    <edge from="28" to="29" />
  </diagram>
  <diagram title="DOS-Win-Mark">
    <node id="1">
      <position x="0.0" y="-0.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-20.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="20.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;diskmark&quot;&gt;0 and &quot;diskmark&quot;&lt;=15 AND &quot;dosmark&quot;&gt;=0 and &quot;dosmark&quot;&lt;40</object>
        </objectContingent>
        <attributeContingent>
          <attribute>Disk WinMark &gt; 0, DOSmark &gt;= 0</attribute>
        </attributeContingent>
      </concept>
    </node>
    <node id="2">
      <position x="40.0" y="40.0" />
      <attributeLabelStyle>
        <offset x="20.0" y="-20.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="20.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;diskmark&quot;&gt;0 and &quot;diskmark&quot;&lt;=15 AND &quot;dosmark&quot;&gt;=40 and &quot;dosmark&quot;&lt;50</object>
        </objectContingent>
        <attributeContingent>
          <attribute>&gt;= 40</attribute>
        </attributeContingent>
      </concept>
    </node>
    <node id="3">
      <position x="80.0" y="80.0" />
      <attributeLabelStyle>
        <offset x="20.0" y="-20.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="20.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;diskmark&quot;&gt;0 and &quot;diskmark&quot;&lt;=15 AND &quot;dosmark&quot;&gt;=50 and &quot;dosmark&quot;&lt;60</object>
        </objectContingent>
        <attributeContingent>
          <attribute>&gt;= 50</attribute>
        </attributeContingent>
      </concept>
    </node>
    <node id="4">
      <position x="120.0" y="120.0" />
      <attributeLabelStyle>
        <offset x="20.0" y="-20.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="20.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;diskmark&quot;&gt;0 and &quot;diskmark&quot;&lt;=15 AND &quot;dosmark&quot;&gt;=60 and &quot;dosmark&quot;&lt;70</object>
        </objectContingent>
        <attributeContingent>
          <attribute>&gt;= 60</attribute>
        </attributeContingent>
      </concept>
    </node>
    <node id="5">
      <position x="160.0" y="160.0" />
      <attributeLabelStyle>
        <offset x="20.0" y="-20.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="20.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;diskmark&quot;&gt;0 and &quot;diskmark&quot;&lt;=15 AND &quot;dosmark&quot;&gt;=70 and &quot;dosmark&quot;&lt;80</object>
        </objectContingent>
        <attributeContingent>
          <attribute>&gt;= 70</attribute>
        </attributeContingent>
      </concept>
    </node>
    <node id="6">
      <position x="200.0" y="200.0" />
      <attributeLabelStyle>
        <offset x="20.0" y="-20.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="20.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;diskmark&quot;&gt;0 and &quot;diskmark&quot;&lt;=15 AND &quot;dosmark&quot;&gt;=80 and &quot;dosmark&quot;&lt;90</object>
        </objectContingent>
        <attributeContingent>
          <attribute>&gt;= 80</attribute>
        </attributeContingent>
      </concept>
    </node>
    <node id="7">
      <position x="240.0" y="240.0" />
      <attributeLabelStyle>
        <offset x="20.0" y="-20.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="20.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;diskmark&quot;&gt;0 and &quot;diskmark&quot;&lt;=15 AND &quot;dosmark&quot;&gt;=90</object>
        </objectContingent>
        <attributeContingent>
          <attribute>&gt;= 90</attribute>
        </attributeContingent>
      </concept>
    </node>
    <node id="8">
      <position x="-40.0" y="40.0" />
      <attributeLabelStyle>
        <offset x="-20.0" y="-20.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>right</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="20.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;diskmark&quot;&gt;15 and &quot;diskmark&quot;&lt;=30 AND &quot;dosmark&quot;&gt;=0 and &quot;dosmark&quot;&lt;40</object>
        </objectContingent>
        <attributeContingent>
          <attribute>&gt; 15</attribute>
        </attributeContingent>
      </concept>
    </node>
    <node id="9">
      <position x="-80.0" y="80.0" />
      <attributeLabelStyle>
        <offset x="-20.0" y="-20.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>right</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="20.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;diskmark&quot;&gt;30 and &quot;diskmark&quot;&lt;=45 AND &quot;dosmark&quot;&gt;=0 and &quot;dosmark&quot;&lt;40</object>
        </objectContingent>
        <attributeContingent>
          <attribute>&gt; 30</attribute>
        </attributeContingent>
      </concept>
    </node>
    <node id="10">
      <position x="-120.0" y="120.0" />
      <attributeLabelStyle>
        <offset x="-20.0" y="-20.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>right</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="20.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;diskmark&quot;&gt;45 and &quot;diskmark&quot;&lt;=60 AND &quot;dosmark&quot;&gt;=0 and &quot;dosmark&quot;&lt;40</object>
        </objectContingent>
        <attributeContingent>
          <attribute>&gt; 45</attribute>
        </attributeContingent>
      </concept>
    </node>
    <node id="11">
      <position x="-160.0" y="160.0" />
      <attributeLabelStyle>
        <offset x="-20.0" y="-20.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>right</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="20.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;diskmark&quot;&gt;60 and &quot;diskmark&quot;&lt;=90 AND &quot;dosmark&quot;&gt;=0 and &quot;dosmark&quot;&lt;40</object>
        </objectContingent>
        <attributeContingent>
          <attribute>&gt; 60</attribute>
        </attributeContingent>
      </concept>
    </node>
    <node id="12">
      <position x="-200.0" y="200.0" />
      <attributeLabelStyle>
        <offset x="-20.0" y="-20.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>right</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="20.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;diskmark&quot;&gt;90 and &quot;diskmark&quot;&lt;=120 AND &quot;dosmark&quot;&gt;=0 and &quot;dosmark&quot;&lt;40</object>
        </objectContingent>
        <attributeContingent>
          <attribute>&gt; 90</attribute>
        </attributeContingent>
      </concept>
    </node>
    <node id="13">
      <position x="-240.0" y="240.0" />
      <attributeLabelStyle>
        <offset x="-20.0" y="-20.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>right</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="20.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;diskmark&quot;&gt;120 AND &quot;dosmark&quot;&gt;=0 and &quot;dosmark&quot;&lt;40</object>
        </objectContingent>
        <attributeContingent>
          <attribute>&gt; 120</attribute>
        </attributeContingent>
      </concept>
    </node>
    <node id="14">
      <position x="0.0" y="80.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="20.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;diskmark&quot;&gt;15 and &quot;diskmark&quot;&lt;=30 AND &quot;dosmark&quot;&gt;=40 and &quot;dosmark&quot;&lt;50</object>
        </objectContingent>
        <attributeContingent />
      </concept>
    </node>
    <node id="15">
      <position x="40.0" y="120.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="20.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;diskmark&quot;&gt;15 and &quot;diskmark&quot;&lt;=30 AND &quot;dosmark&quot;&gt;=50 and &quot;dosmark&quot;&lt;60</object>
        </objectContingent>
        <attributeContingent />
      </concept>
    </node>
    <node id="16">
      <position x="80.0" y="160.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="20.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;diskmark&quot;&gt;15 and &quot;diskmark&quot;&lt;=30 AND &quot;dosmark&quot;&gt;=60 and &quot;dosmark&quot;&lt;70</object>
        </objectContingent>
        <attributeContingent />
      </concept>
    </node>
    <node id="17">
      <position x="120.0" y="200.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="20.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;diskmark&quot;&gt;15 and &quot;diskmark&quot;&lt;=30 AND &quot;dosmark&quot;&gt;=70 and &quot;dosmark&quot;&lt;80</object>
        </objectContingent>
        <attributeContingent />
      </concept>
    </node>
    <node id="18">
      <position x="160.0" y="240.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="20.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;diskmark&quot;&gt;15 and &quot;diskmark&quot;&lt;=30 AND &quot;dosmark&quot;&gt;=80 and &quot;dosmark&quot;&lt;90</object>
        </objectContingent>
        <attributeContingent />
      </concept>
    </node>
    <node id="19">
      <position x="200.0" y="280.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="20.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;diskmark&quot;&gt;15 and &quot;diskmark&quot;&lt;=30 AND &quot;dosmark&quot;&gt;=90</object>
        </objectContingent>
        <attributeContingent />
      </concept>
    </node>
    <node id="20">
      <position x="-40.0" y="120.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="20.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;diskmark&quot;&gt;30 and &quot;diskmark&quot;&lt;=45 AND &quot;dosmark&quot;&gt;=40 and &quot;dosmark&quot;&lt;50</object>
        </objectContingent>
        <attributeContingent />
      </concept>
    </node>
    <node id="21">
      <position x="-80.0" y="160.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="20.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;diskmark&quot;&gt;45 and &quot;diskmark&quot;&lt;=60 AND &quot;dosmark&quot;&gt;=40 and &quot;dosmark&quot;&lt;50</object>
        </objectContingent>
        <attributeContingent />
      </concept>
    </node>
    <node id="22">
      <position x="-120.0" y="200.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="20.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;diskmark&quot;&gt;60 and &quot;diskmark&quot;&lt;=90 AND &quot;dosmark&quot;&gt;=40 and &quot;dosmark&quot;&lt;50</object>
        </objectContingent>
        <attributeContingent />
      </concept>
    </node>
    <node id="23">
      <position x="-160.0" y="240.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="20.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;diskmark&quot;&gt;90 and &quot;diskmark&quot;&lt;=120 AND &quot;dosmark&quot;&gt;=40 and &quot;dosmark&quot;&lt;50</object>
        </objectContingent>
        <attributeContingent />
      </concept>
    </node>
    <node id="24">
      <position x="-200.0" y="280.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="20.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;diskmark&quot;&gt;120 AND &quot;dosmark&quot;&gt;=40 and &quot;dosmark&quot;&lt;50</object>
        </objectContingent>
        <attributeContingent />
      </concept>
    </node>
    <node id="25">
      <position x="0.0" y="160.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="20.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;diskmark&quot;&gt;30 and &quot;diskmark&quot;&lt;=45 AND &quot;dosmark&quot;&gt;=50 and &quot;dosmark&quot;&lt;60</object>
        </objectContingent>
        <attributeContingent />
      </concept>
    </node>
    <node id="26">
      <position x="40.0" y="200.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="20.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;diskmark&quot;&gt;30 and &quot;diskmark&quot;&lt;=45 AND &quot;dosmark&quot;&gt;=60 and &quot;dosmark&quot;&lt;70</object>
        </objectContingent>
        <attributeContingent />
      </concept>
    </node>
    <node id="27">
      <position x="80.0" y="240.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="20.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;diskmark&quot;&gt;30 and &quot;diskmark&quot;&lt;=45 AND &quot;dosmark&quot;&gt;=70 and &quot;dosmark&quot;&lt;80</object>
        </objectContingent>
        <attributeContingent />
      </concept>
    </node>
    <node id="28">
      <position x="120.0" y="280.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="20.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;diskmark&quot;&gt;30 and &quot;diskmark&quot;&lt;=45 AND &quot;dosmark&quot;&gt;=80 and &quot;dosmark&quot;&lt;90</object>
        </objectContingent>
        <attributeContingent />
      </concept>
    </node>
    <node id="29">
      <position x="160.0" y="320.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="20.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;diskmark&quot;&gt;30 and &quot;diskmark&quot;&lt;=45 AND &quot;dosmark&quot;&gt;=90</object>
        </objectContingent>
        <attributeContingent />
      </concept>
    </node>
    <node id="30">
      <position x="-40.0" y="200.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="20.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;diskmark&quot;&gt;45 and &quot;diskmark&quot;&lt;=60 AND &quot;dosmark&quot;&gt;=50 and &quot;dosmark&quot;&lt;60</object>
        </objectContingent>
        <attributeContingent />
      </concept>
    </node>
    <node id="31">
      <position x="-80.0" y="240.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="20.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;diskmark&quot;&gt;60 and &quot;diskmark&quot;&lt;=90 AND &quot;dosmark&quot;&gt;=50 and &quot;dosmark&quot;&lt;60</object>
        </objectContingent>
        <attributeContingent />
      </concept>
    </node>
    <node id="32">
      <position x="-120.0" y="280.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="20.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;diskmark&quot;&gt;90 and &quot;diskmark&quot;&lt;=120 AND &quot;dosmark&quot;&gt;=50 and &quot;dosmark&quot;&lt;60</object>
        </objectContingent>
        <attributeContingent />
      </concept>
    </node>
    <node id="33">
      <position x="-160.0" y="320.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="20.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;diskmark&quot;&gt;120 AND &quot;dosmark&quot;&gt;=50 and &quot;dosmark&quot;&lt;60</object>
        </objectContingent>
        <attributeContingent />
      </concept>
    </node>
    <node id="34">
      <position x="0.0" y="240.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="20.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;diskmark&quot;&gt;45 and &quot;diskmark&quot;&lt;=60 AND &quot;dosmark&quot;&gt;=60 and &quot;dosmark&quot;&lt;70</object>
        </objectContingent>
        <attributeContingent />
      </concept>
    </node>
    <node id="35">
      <position x="40.0" y="280.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="20.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;diskmark&quot;&gt;45 and &quot;diskmark&quot;&lt;=60 AND &quot;dosmark&quot;&gt;=70 and &quot;dosmark&quot;&lt;80</object>
        </objectContingent>
        <attributeContingent />
      </concept>
    </node>
    <node id="36">
      <position x="80.0" y="320.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="20.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;diskmark&quot;&gt;45 and &quot;diskmark&quot;&lt;=60 AND &quot;dosmark&quot;&gt;=80 and &quot;dosmark&quot;&lt;90</object>
        </objectContingent>
        <attributeContingent />
      </concept>
    </node>
    <node id="37">
      <position x="120.0" y="360.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="20.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;diskmark&quot;&gt;45 and &quot;diskmark&quot;&lt;=60 AND &quot;dosmark&quot;&gt;=90</object>
        </objectContingent>
        <attributeContingent />
      </concept>
    </node>
    <node id="38">
      <position x="-40.0" y="280.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="20.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;diskmark&quot;&gt;60 and &quot;diskmark&quot;&lt;=90 AND &quot;dosmark&quot;&gt;=60 and &quot;dosmark&quot;&lt;70</object>
        </objectContingent>
        <attributeContingent />
      </concept>
    </node>
    <node id="39">
      <position x="-80.0" y="320.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="20.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;diskmark&quot;&gt;90 and &quot;diskmark&quot;&lt;=120 AND &quot;dosmark&quot;&gt;=60 and &quot;dosmark&quot;&lt;70</object>
        </objectContingent>
        <attributeContingent />
      </concept>
    </node>
    <node id="40">
      <position x="-120.0" y="360.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="20.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;diskmark&quot;&gt;120 AND &quot;dosmark&quot;&gt;=60 and &quot;dosmark&quot;&lt;70</object>
        </objectContingent>
        <attributeContingent />
      </concept>
    </node>
    <node id="41">
      <position x="0.0" y="320.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="20.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;diskmark&quot;&gt;60 and &quot;diskmark&quot;&lt;=90 AND &quot;dosmark&quot;&gt;=70 and &quot;dosmark&quot;&lt;80</object>
        </objectContingent>
        <attributeContingent />
      </concept>
    </node>
    <node id="42">
      <position x="40.0" y="360.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="20.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;diskmark&quot;&gt;60 and &quot;diskmark&quot;&lt;=90 AND &quot;dosmark&quot;&gt;=80 and &quot;dosmark&quot;&lt;90</object>
        </objectContingent>
        <attributeContingent />
      </concept>
    </node>
    <node id="43">
      <position x="80.0" y="400.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="20.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;diskmark&quot;&gt;60 and &quot;diskmark&quot;&lt;=90 AND &quot;dosmark&quot;&gt;=90</object>
        </objectContingent>
        <attributeContingent />
      </concept>
    </node>
    <node id="44">
      <position x="-40.0" y="360.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="20.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;diskmark&quot;&gt;90 and &quot;diskmark&quot;&lt;=120 AND &quot;dosmark&quot;&gt;=70 and &quot;dosmark&quot;&lt;80</object>
        </objectContingent>
        <attributeContingent />
      </concept>
    </node>
    <node id="45">
      <position x="-80.0" y="400.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="20.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;diskmark&quot;&gt;120 AND &quot;dosmark&quot;&gt;=70 and &quot;dosmark&quot;&lt;80</object>
        </objectContingent>
        <attributeContingent />
      </concept>
    </node>
    <node id="46">
      <position x="0.0" y="400.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="20.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;diskmark&quot;&gt;90 and &quot;diskmark&quot;&lt;=120 AND &quot;dosmark&quot;&gt;=80 and &quot;dosmark&quot;&lt;90</object>
        </objectContingent>
        <attributeContingent />
      </concept>
    </node>
    <node id="47">
      <position x="40.0" y="440.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="20.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;diskmark&quot;&gt;90 and &quot;diskmark&quot;&lt;=120 AND &quot;dosmark&quot;&gt;=90</object>
        </objectContingent>
        <attributeContingent />
      </concept>
    </node>
    <node id="48">
      <position x="-40.0" y="440.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="20.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;diskmark&quot;&gt;120 AND &quot;dosmark&quot;&gt;=80 and &quot;dosmark&quot;&lt;90</object>
        </objectContingent>
        <attributeContingent />
      </concept>
    </node>
    <node id="49">
      <position x="0.0" y="480.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="20.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;diskmark&quot;&gt;120 AND &quot;dosmark&quot;&gt;=90</object>
        </objectContingent>
        <attributeContingent />
      </concept>
    </node>
    <edge from="1" to="2" />
    <edge from="1" to="8" />
    <edge from="2" to="3" />
    <edge from="2" to="14" />
    <edge from="3" to="4" />
    <edge from="3" to="15" />
    <edge from="4" to="5" />
    <edge from="4" to="16" />
    <edge from="5" to="6" />
    <edge from="5" to="17" />
    <edge from="6" to="7" />
    <edge from="6" to="18" />
    <edge from="7" to="19" />
    <edge from="8" to="9" />
    <edge from="8" to="14" />
    <edge from="9" to="10" />
    <edge from="9" to="20" />
    <edge from="10" to="11" />
    <edge from="10" to="21" />
    <edge from="11" to="12" />
    <edge from="11" to="22" />
    <edge from="12" to="13" />
    <edge from="12" to="23" />
    <edge from="13" to="24" />
    <edge from="14" to="15" />
    <edge from="14" to="20" />
    <edge from="15" to="16" />
    <edge from="15" to="25" />
    <edge from="16" to="17" />
    <edge from="16" to="26" />
    <edge from="17" to="18" />
    <edge from="17" to="27" />
    <edge from="18" to="19" />
    <edge from="18" to="28" />
    <edge from="19" to="29" />
    <edge from="20" to="21" />
    <edge from="20" to="25" />
    <edge from="21" to="22" />
    <edge from="21" to="30" />
    <edge from="22" to="23" />
    <edge from="22" to="31" />
    <edge from="23" to="24" />
    <edge from="23" to="32" />
    <edge from="24" to="33" />
    <edge from="25" to="26" />
    <edge from="25" to="30" />
    <edge from="26" to="27" />
    <edge from="26" to="34" />
    <edge from="27" to="28" />
    <edge from="27" to="35" />
    <edge from="28" to="29" />
    <edge from="28" to="36" />
    <edge from="29" to="37" />
    <edge from="30" to="31" />
    <edge from="30" to="34" />
    <edge from="31" to="32" />
    <edge from="31" to="38" />
    <edge from="32" to="33" />
    <edge from="32" to="39" />
    <edge from="33" to="40" />
    <edge from="34" to="35" />
    <edge from="34" to="38" />
    <edge from="35" to="36" />
    <edge from="35" to="41" />
    <edge from="36" to="37" />
    <edge from="36" to="42" />
    <edge from="37" to="43" />
    <edge from="38" to="39" />
    <edge from="38" to="41" />
    <edge from="39" to="40" />
    <edge from="39" to="44" />
    <edge from="40" to="45" />
    <edge from="41" to="42" />
    <edge from="41" to="44" />
    <edge from="42" to="43" />
    <edge from="42" to="46" />
    <edge from="43" to="47" />
    <edge from="44" to="45" />
    <edge from="44" to="46" />
    <edge from="45" to="48" />
    <edge from="46" to="47" />
    <edge from="46" to="48" />
    <edge from="47" to="49" />
    <edge from="48" to="49" />
  </diagram>
  <diagram title="Means of distribution">
    <description>
      <html>
        <body>
          <h1>Means of Distribution</h1>
          
                    Shows how the PCs are sold.
                
        </body>
      </html>
    </description>
    <node id="1">
      <position x="0.0" y="-0.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent />
        <attributeContingent />
      </concept>
    </node>
    <node id="2">
      <position x="-70.0" y="70.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-17.5" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>right</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent />
        <attributeContingent>
          <attribute>direct sales</attribute>
        </attributeContingent>
      </concept>
    </node>
    <node id="3">
      <position x="-140.0" y="140.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-17.5" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="21.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;directsales&quot;=1 and &quot;dealer&quot;=0</object>
        </objectContingent>
        <attributeContingent>
          <attribute>only direct sales</attribute>
        </attributeContingent>
      </concept>
    </node>
    <node id="4">
      <position x="70.0" y="70.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-17.5" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent />
        <attributeContingent>
          <attribute>shops</attribute>
        </attributeContingent>
      </concept>
    </node>
    <node id="5">
      <position x="140.0" y="140.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-17.5" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="21.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;directsales&quot;=0 and &quot;dealer&quot;=1</object>
        </objectContingent>
        <attributeContingent>
          <attribute>only shops</attribute>
        </attributeContingent>
      </concept>
    </node>
    <node id="6">
      <position x="0.0" y="140.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-17.5" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="21.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;directsales&quot;=1 and &quot;dealer&quot;=1</object>
        </objectContingent>
        <attributeContingent>
          <attribute>both forms</attribute>
        </attributeContingent>
      </concept>
    </node>
    <node id="7">
      <position x="0.0" y="210.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent />
        <attributeContingent />
      </concept>
    </node>
    <edge from="1" to="2" />
    <edge from="1" to="4" />
    <edge from="2" to="3" />
    <edge from="2" to="6" />
    <edge from="3" to="7" />
    <edge from="4" to="5" />
    <edge from="4" to="6" />
    <edge from="5" to="7" />
    <edge from="6" to="7" />
  </diagram>
  <diagram title="Cases">
    <description>
      <html>
        <body>
          <h1>Case Types</h1>
          
                    Shows which types of cases are used for the PCs.
                
        </body>
      </html>
    </description>
    <node id="1">
      <position x="0.0" y="-0.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent />
        <attributeContingent />
      </concept>
    </node>
    <node id="2">
      <position x="50.0" y="50.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-8.75" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="7.5" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;typeCase&quot; = 'Desktop'</object>
        </objectContingent>
        <attributeContingent>
          <attribute>Desktop</attribute>
        </attributeContingent>
      </concept>
    </node>
    <node id="3">
      <position x="100.0" y="100.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-8.75" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="7.5" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;typeCase&quot; = 'Slimline'</object>
        </objectContingent>
        <attributeContingent>
          <attribute>Slimline</attribute>
        </attributeContingent>
      </concept>
    </node>
    <node id="4">
      <position x="0.0" y="100.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-8.75" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="7.5" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;typeCase&quot; = 'Small-footprint'</object>
        </objectContingent>
        <attributeContingent>
          <attribute>Small-footprint</attribute>
        </attributeContingent>
      </concept>
    </node>
    <node id="5">
      <position x="-50.0" y="50.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-8.75" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="7.5" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;typeCase&quot; = 'Tower'</object>
        </objectContingent>
        <attributeContingent>
          <attribute>Tower</attribute>
        </attributeContingent>
      </concept>
    </node>
    <node id="6">
      <position x="-100.0" y="100.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-8.75" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="7.5" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;typeCase&quot; = 'Minitower'</object>
        </objectContingent>
        <attributeContingent>
          <attribute>Mini-Tower</attribute>
        </attributeContingent>
      </concept>
    </node>
    <node id="7">
      <position x="0.0" y="175.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent />
        <attributeContingent />
      </concept>
    </node>
    <edge from="1" to="2" />
    <edge from="1" to="5" />
    <edge from="2" to="3" />
    <edge from="2" to="4" />
    <edge from="3" to="7" />
    <edge from="4" to="7" />
    <edge from="5" to="6" />
    <edge from="6" to="7" />
  </diagram>
  <diagram title="Accessible drives">
    <node id="1">
      <position x="0.0" y="-0.0" />
      <attributeLabelStyle>
        <offset x="-17.5" y="-12.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>right</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="15.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;freeDriveSlots&quot; = 0</object>
        </objectContingent>
        <attributeContingent>
          <attribute>no 5¼" bay</attribute>
          <attribute>no 3½" bay</attribute>
        </attributeContingent>
      </concept>
    </node>
    <node id="2">
      <position x="50.0" y="50.0" />
      <attributeLabelStyle>
        <offset x="15.0" y="-15.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="15.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;freeDriveSlots&quot; = 1</object>
        </objectContingent>
        <attributeContingent>
          <attribute>one 3½" bay</attribute>
        </attributeContingent>
      </concept>
    </node>
    <node id="3">
      <position x="100.0" y="100.0" />
      <attributeLabelStyle>
        <offset x="15.0" y="-15.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="15.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;freeDriveSlots&quot; = 2</object>
        </objectContingent>
        <attributeContingent>
          <attribute>two 3½" bays</attribute>
        </attributeContingent>
      </concept>
    </node>
    <node id="4">
      <position x="150.0" y="150.0" />
      <attributeLabelStyle>
        <offset x="15.0" y="-15.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="15.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;freeDriveSlots&quot; = 3</object>
        </objectContingent>
        <attributeContingent>
          <attribute>three 3½" bays</attribute>
        </attributeContingent>
      </concept>
    </node>
    <node id="5">
      <position x="-50.0" y="50.0" />
      <attributeLabelStyle>
        <offset x="-4.5" y="-14.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>right</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="15.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;freeDriveSlots&quot; = 10</object>
        </objectContingent>
        <attributeContingent>
          <attribute>one 5¼" bay</attribute>
        </attributeContingent>
      </concept>
    </node>
    <node id="6">
      <position x="0.0" y="100.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="15.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;freeDriveSlots&quot; = 11</object>
        </objectContingent>
        <attributeContingent />
      </concept>
    </node>
    <node id="7">
      <position x="50.0" y="150.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="15.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;freeDriveSlots&quot; = 12</object>
        </objectContingent>
        <attributeContingent />
      </concept>
    </node>
    <node id="8">
      <position x="100.0" y="200.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="15.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;freeDriveSlots&quot; = 13</object>
        </objectContingent>
        <attributeContingent />
      </concept>
    </node>
    <node id="9">
      <position x="-100.0" y="100.0" />
      <attributeLabelStyle>
        <offset x="-5.5" y="-15.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>right</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="15.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;freeDriveSlots&quot; = 20</object>
        </objectContingent>
        <attributeContingent>
          <attribute>two 5¼" bays</attribute>
        </attributeContingent>
      </concept>
    </node>
    <node id="10">
      <position x="-50.0" y="150.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="15.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;freeDriveSlots&quot; = 21</object>
        </objectContingent>
        <attributeContingent />
      </concept>
    </node>
    <node id="11">
      <position x="0.0" y="200.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="15.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;freeDriveSlots&quot; = 22</object>
        </objectContingent>
        <attributeContingent />
      </concept>
    </node>
    <node id="12">
      <position x="50.0" y="250.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="15.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;freeDriveSlots&quot; = 23</object>
        </objectContingent>
        <attributeContingent />
      </concept>
    </node>
    <node id="13">
      <position x="-150.0" y="150.0" />
      <attributeLabelStyle>
        <offset x="-11.0" y="-14.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>right</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="15.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;freeDriveSlots&quot; = 30</object>
        </objectContingent>
        <attributeContingent>
          <attribute>three 5¼" bays</attribute>
        </attributeContingent>
      </concept>
    </node>
    <node id="14">
      <position x="-100.0" y="200.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="15.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;freeDriveSlots&quot; = 31</object>
        </objectContingent>
        <attributeContingent />
      </concept>
    </node>
    <node id="15">
      <position x="-50.0" y="250.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="15.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;freeDriveSlots&quot; = 32</object>
        </objectContingent>
        <attributeContingent />
      </concept>
    </node>
    <node id="16">
      <position x="0.0" y="300.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="15.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;freeDriveSlots&quot; = 33</object>
        </objectContingent>
        <attributeContingent />
      </concept>
    </node>
    <node id="17">
      <position x="-200.0" y="200.0" />
      <attributeLabelStyle>
        <offset x="-8.0" y="-16.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>right</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="15.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;freeDriveSlots&quot; = 40</object>
        </objectContingent>
        <attributeContingent>
          <attribute>four 5¼" bays</attribute>
        </attributeContingent>
      </concept>
    </node>
    <node id="18">
      <position x="-150.0" y="250.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="15.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;freeDriveSlots&quot; = 41</object>
        </objectContingent>
        <attributeContingent />
      </concept>
    </node>
    <node id="19">
      <position x="-100.0" y="300.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="15.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;freeDriveSlots&quot; = 42</object>
        </objectContingent>
        <attributeContingent />
      </concept>
    </node>
    <node id="20">
      <position x="-50.0" y="350.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="15.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;freeDriveSlots&quot; = 43</object>
        </objectContingent>
        <attributeContingent />
      </concept>
    </node>
    <node id="21">
      <position x="-250.0" y="250.0" />
      <attributeLabelStyle>
        <offset x="-7.5" y="-12.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>right</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="15.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;freeDriveSlots&quot; = 50</object>
        </objectContingent>
        <attributeContingent>
          <attribute>five 5¼" bays</attribute>
        </attributeContingent>
      </concept>
    </node>
    <node id="22">
      <position x="-200.0" y="300.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="15.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;freeDriveSlots&quot; = 51</object>
        </objectContingent>
        <attributeContingent />
      </concept>
    </node>
    <node id="23">
      <position x="-150.0" y="350.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="15.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;freeDriveSlots&quot; = 52</object>
        </objectContingent>
        <attributeContingent />
      </concept>
    </node>
    <node id="24">
      <position x="-100.0" y="400.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="15.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;freeDriveSlots&quot; = 53</object>
        </objectContingent>
        <attributeContingent />
      </concept>
    </node>
    <node id="25">
      <position x="-300.0" y="300.0" />
      <attributeLabelStyle>
        <offset x="-7.0" y="-15.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>right</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="15.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;freeDriveSlots&quot; = 60</object>
        </objectContingent>
        <attributeContingent>
          <attribute>six 5¼" bays</attribute>
        </attributeContingent>
      </concept>
    </node>
    <node id="26">
      <position x="-250.0" y="350.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="15.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;freeDriveSlots&quot; = 61</object>
        </objectContingent>
        <attributeContingent />
      </concept>
    </node>
    <node id="27">
      <position x="-200.0" y="400.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="15.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;freeDriveSlots&quot; = 62</object>
        </objectContingent>
        <attributeContingent />
      </concept>
    </node>
    <node id="28">
      <position x="-150.0" y="450.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="15.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;freeDriveSlots&quot; = 63</object>
        </objectContingent>
        <attributeContingent />
      </concept>
    </node>
    <node id="29">
      <position x="-350.0" y="350.0" />
      <attributeLabelStyle>
        <offset x="-10.0" y="-14.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>right</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="15.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;freeDriveSlots&quot; = 70</object>
        </objectContingent>
        <attributeContingent>
          <attribute>seven 5¼" bays</attribute>
        </attributeContingent>
      </concept>
    </node>
    <node id="30">
      <position x="-300.0" y="400.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="15.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;freeDriveSlots&quot; = 71</object>
        </objectContingent>
        <attributeContingent />
      </concept>
    </node>
    <node id="31">
      <position x="-250.0" y="450.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="15.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;freeDriveSlots&quot; = 72</object>
        </objectContingent>
        <attributeContingent />
      </concept>
    </node>
    <node id="32">
      <position x="-200.0" y="500.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="15.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;freeDriveSlots&quot; = 73</object>
        </objectContingent>
        <attributeContingent />
      </concept>
    </node>
    <edge from="1" to="2" />
    <edge from="1" to="5" />
    <edge from="2" to="3" />
    <edge from="2" to="6" />
    <edge from="3" to="4" />
    <edge from="3" to="7" />
    <edge from="4" to="8" />
    <edge from="5" to="6" />
    <edge from="5" to="9" />
    <edge from="6" to="7" />
    <edge from="6" to="10" />
    <edge from="7" to="8" />
    <edge from="7" to="11" />
    <edge from="8" to="12" />
    <edge from="9" to="10" />
    <edge from="9" to="13" />
    <edge from="10" to="11" />
    <edge from="10" to="14" />
    <edge from="11" to="12" />
    <edge from="11" to="15" />
    <edge from="12" to="16" />
    <edge from="13" to="14" />
    <edge from="13" to="17" />
    <edge from="14" to="15" />
    <edge from="14" to="18" />
    <edge from="15" to="16" />
    <edge from="15" to="19" />
    <edge from="16" to="20" />
    <edge from="17" to="18" />
    <edge from="17" to="21" />
    <edge from="18" to="19" />
    <edge from="18" to="22" />
    <edge from="19" to="20" />
    <edge from="19" to="23" />
    <edge from="20" to="24" />
    <edge from="21" to="22" />
    <edge from="21" to="25" />
    <edge from="22" to="23" />
    <edge from="22" to="26" />
    <edge from="23" to="24" />
    <edge from="23" to="27" />
    <edge from="24" to="28" />
    <edge from="25" to="26" />
    <edge from="25" to="29" />
    <edge from="26" to="27" />
    <edge from="26" to="30" />
    <edge from="27" to="28" />
    <edge from="27" to="31" />
    <edge from="28" to="32" />
    <edge from="29" to="30" />
    <edge from="30" to="31" />
    <edge from="31" to="32" />
  </diagram>
  <diagram title="Graphic card">
    <node id="1">
      <position x="-70.0" y="-0.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent />
        <attributeContingent />
      </concept>
    </node>
    <node id="2">
      <position x="-280.0" y="105.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-8.75" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="10.5" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;graphiccard&quot; = 'ISA'</object>
        </objectContingent>
        <attributeContingent>
          <attribute>ISA Bus</attribute>
        </attributeContingent>
      </concept>
    </node>
    <node id="3">
      <position x="-280.0" y="175.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-8.75" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="10.5" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;graphiccard&quot; = 'EISA'</object>
        </objectContingent>
        <attributeContingent>
          <attribute>EISA Bus</attribute>
        </attributeContingent>
      </concept>
    </node>
    <node id="4">
      <position x="-70.0" y="70.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-8.75" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="10.5" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;graphiccard&quot; = 'Local Bus'</object>
        </objectContingent>
        <attributeContingent>
          <attribute>Local Bus</attribute>
        </attributeContingent>
      </concept>
    </node>
    <node id="5">
      <position x="70.0" y="140.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-8.75" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="10.5" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;graphiccard&quot; = 'Motherboard'</object>
        </objectContingent>
        <attributeContingent>
          <attribute>Motherboard</attribute>
        </attributeContingent>
      </concept>
    </node>
    <node id="6">
      <position x="-210.0" y="140.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-8.75" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="10.5" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;graphiccard&quot; = 'MCA'</object>
        </objectContingent>
        <attributeContingent>
          <attribute>MCA</attribute>
        </attributeContingent>
      </concept>
    </node>
    <node id="7">
      <position x="0.0" y="140.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-8.75" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="10.5" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;graphiccard&quot; = 'VESA Local Bus'</object>
        </objectContingent>
        <attributeContingent>
          <attribute>VESA Local Bus</attribute>
        </attributeContingent>
      </concept>
    </node>
    <node id="8">
      <position x="-140.0" y="140.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-8.75" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="10.5" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;graphiccard&quot; = 'Proprietary Local Bus'</object>
        </objectContingent>
        <attributeContingent>
          <attribute>Proprietary Local Bus</attribute>
        </attributeContingent>
      </concept>
    </node>
    <node id="9">
      <position x="-70.0" y="140.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-8.75" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="10.5" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;graphiccard&quot; = 'UBSA Local Bus'</object>
        </objectContingent>
        <attributeContingent>
          <attribute>UBSA Local BUS</attribute>
        </attributeContingent>
      </concept>
    </node>
    <node id="10">
      <position x="-70.0" y="245.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent />
        <attributeContingent />
      </concept>
    </node>
    <edge from="1" to="2" />
    <edge from="1" to="4" />
    <edge from="1" to="5" />
    <edge from="1" to="6" />
    <edge from="2" to="3" />
    <edge from="3" to="10" />
    <edge from="4" to="7" />
    <edge from="4" to="8" />
    <edge from="4" to="9" />
    <edge from="5" to="10" />
    <edge from="6" to="10" />
    <edge from="7" to="10" />
    <edge from="8" to="10" />
    <edge from="9" to="10" />
  </diagram>
  <diagram title="Ports on the mainboard">
    <node id="1">
      <position x="-30.0" y="-0.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="9.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;ports&quot; = 'Keine'</object>
        </objectContingent>
        <attributeContingent />
      </concept>
    </node>
    <node id="2">
      <position x="-30.0" y="60.0" />
      <attributeLabelStyle>
        <offset x="-6.300000000000001" y="-9.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>right</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="9.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;ports&quot; = '1,1,0'</object>
        </objectContingent>
        <attributeContingent>
          <attribute>one serial port</attribute>
          <attribute>one parallel port</attribute>
        </attributeContingent>
      </concept>
    </node>
    <node id="3">
      <position x="-30.0" y="120.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-10.5" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="9.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;ports&quot; = '1,1,1'</object>
        </objectContingent>
        <attributeContingent>
          <attribute>one mouse-port</attribute>
        </attributeContingent>
      </concept>
    </node>
    <node id="4">
      <position x="30.0" y="120.0" />
      <attributeLabelStyle>
        <offset x="4.800000000000001" y="-8.100000000000001" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="9.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;ports&quot; = '1,2,0'</object>
        </objectContingent>
        <attributeContingent>
          <attribute>two serial ports</attribute>
        </attributeContingent>
      </concept>
    </node>
    <node id="5">
      <position x="30.0" y="180.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="9.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;ports&quot; = '1,2,1'</object>
        </objectContingent>
        <attributeContingent />
      </concept>
    </node>
    <node id="6">
      <position x="-90.0" y="120.0" />
      <attributeLabelStyle>
        <offset x="-3.5999999999999996" y="-9.600000000000001" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>right</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="9.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;ports&quot; = '2,1,0'</object>
        </objectContingent>
        <attributeContingent>
          <attribute>two parallel ports</attribute>
        </attributeContingent>
      </concept>
    </node>
    <node id="7">
      <position x="-90.0" y="180.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="9.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;ports&quot; = '2,1,1'</object>
        </objectContingent>
        <attributeContingent />
      </concept>
    </node>
    <node id="8">
      <position x="-30.0" y="240.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent />
        <attributeContingent />
      </concept>
    </node>
    <edge from="1" to="2" />
    <edge from="2" to="3" />
    <edge from="2" to="4" />
    <edge from="2" to="6" />
    <edge from="3" to="5" />
    <edge from="3" to="7" />
    <edge from="4" to="5" />
    <edge from="5" to="8" />
    <edge from="6" to="7" />
    <edge from="7" to="8" />
  </diagram>
  <diagram title="Internal drive bays">
    <node id="1">
      <position x="0.0" y="-0.0" />
      <attributeLabelStyle>
        <offset x="-11.6" y="-13.6" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>right</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="12.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;internalDriveSlots&quot; = 0</object>
        </objectContingent>
        <attributeContingent>
          <attribute>no 3½" bay</attribute>
          <attribute>no 5¼" bay</attribute>
        </attributeContingent>
      </concept>
    </node>
    <node id="2">
      <position x="-40.0" y="40.0" />
      <attributeLabelStyle>
        <offset x="-14.0" y="-12.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>right</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="12.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;internalDriveSlots&quot; = 10</object>
        </objectContingent>
        <attributeContingent>
          <attribute>one 5½" bay</attribute>
        </attributeContingent>
      </concept>
    </node>
    <node id="3">
      <position x="-80.0" y="80.0" />
      <attributeLabelStyle>
        <offset x="-10.4" y="-14.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>right</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="12.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;internalDriveSlots&quot; = 20</object>
        </objectContingent>
        <attributeContingent>
          <attribute>two 5½" bays</attribute>
        </attributeContingent>
      </concept>
    </node>
    <node id="4">
      <position x="-120.0" y="120.0" />
      <attributeLabelStyle>
        <offset x="-11.2" y="-13.6" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>right</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="12.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;internalDriveSlots&quot; = 30</object>
        </objectContingent>
        <attributeContingent>
          <attribute>three 5½" bays</attribute>
        </attributeContingent>
      </concept>
    </node>
    <node id="5">
      <position x="40.0" y="40.0" />
      <attributeLabelStyle>
        <offset x="12.0" y="-12.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="12.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;internalDriveSlots&quot; = 1</object>
        </objectContingent>
        <attributeContingent>
          <attribute>one 3¼" bay</attribute>
        </attributeContingent>
      </concept>
    </node>
    <node id="6">
      <position x="0.0" y="80.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="12.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;internalDriveSlots&quot; = 11</object>
        </objectContingent>
        <attributeContingent />
      </concept>
    </node>
    <node id="7">
      <position x="-40.0" y="120.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="12.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;internalDriveSlots&quot; = 21</object>
        </objectContingent>
        <attributeContingent />
      </concept>
    </node>
    <node id="8">
      <position x="-80.0" y="160.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="12.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;internalDriveSlots&quot; = 31</object>
        </objectContingent>
        <attributeContingent />
      </concept>
    </node>
    <node id="9">
      <position x="80.0" y="80.0" />
      <attributeLabelStyle>
        <offset x="12.0" y="-12.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="12.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;internalDriveSlots&quot; = 2</object>
        </objectContingent>
        <attributeContingent>
          <attribute>two 3¼" bays</attribute>
        </attributeContingent>
      </concept>
    </node>
    <node id="10">
      <position x="40.0" y="120.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="12.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;internalDriveSlots&quot; = 12</object>
        </objectContingent>
        <attributeContingent />
      </concept>
    </node>
    <node id="11">
      <position x="0.0" y="160.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="12.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;internalDriveSlots&quot; = 22</object>
        </objectContingent>
        <attributeContingent />
      </concept>
    </node>
    <node id="12">
      <position x="-40.0" y="200.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="12.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;internalDriveSlots&quot; = 32</object>
        </objectContingent>
        <attributeContingent />
      </concept>
    </node>
    <node id="13">
      <position x="120.0" y="120.0" />
      <attributeLabelStyle>
        <offset x="12.0" y="-12.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="12.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;internalDriveSlots&quot; = 3</object>
        </objectContingent>
        <attributeContingent>
          <attribute>three 3¼" bays</attribute>
        </attributeContingent>
      </concept>
    </node>
    <node id="14">
      <position x="80.0" y="160.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="12.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;internalDriveSlots&quot; = 13</object>
        </objectContingent>
        <attributeContingent />
      </concept>
    </node>
    <node id="15">
      <position x="40.0" y="200.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="12.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;internalDriveSlots&quot; = 23</object>
        </objectContingent>
        <attributeContingent />
      </concept>
    </node>
    <node id="16">
      <position x="0.0" y="240.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="12.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;internalDriveSlots&quot; = 33</object>
        </objectContingent>
        <attributeContingent />
      </concept>
    </node>
    <node id="17">
      <position x="-160.0" y="160.0" />
      <attributeLabelStyle>
        <offset x="-12.0" y="-13.2" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>right</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="12.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;internalDriveSlots&quot; = 40</object>
        </objectContingent>
        <attributeContingent>
          <attribute>four 5½" bays</attribute>
        </attributeContingent>
      </concept>
    </node>
    <node id="18">
      <position x="-120.0" y="200.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="12.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;internalDriveSlots&quot; = 41</object>
        </objectContingent>
        <attributeContingent />
      </concept>
    </node>
    <node id="19">
      <position x="-80.0" y="240.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="12.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;internalDriveSlots&quot; = 42</object>
        </objectContingent>
        <attributeContingent />
      </concept>
    </node>
    <node id="20">
      <position x="-40.0" y="280.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="12.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;internalDriveSlots&quot; = 43</object>
        </objectContingent>
        <attributeContingent />
      </concept>
    </node>
    <node id="21">
      <position x="160.0" y="160.0" />
      <attributeLabelStyle>
        <offset x="12.0" y="-12.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="12.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;internalDriveSlots&quot; = 4</object>
        </objectContingent>
        <attributeContingent>
          <attribute>four 3¼" bays</attribute>
        </attributeContingent>
      </concept>
    </node>
    <node id="22">
      <position x="120.0" y="200.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="12.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;internalDriveSlots&quot; = 14</object>
        </objectContingent>
        <attributeContingent />
      </concept>
    </node>
    <node id="23">
      <position x="80.0" y="240.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="12.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;internalDriveSlots&quot; = 24</object>
        </objectContingent>
        <attributeContingent />
      </concept>
    </node>
    <node id="24">
      <position x="40.0" y="280.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="12.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;internalDriveSlots&quot; = 34</object>
        </objectContingent>
        <attributeContingent />
      </concept>
    </node>
    <node id="25">
      <position x="0.0" y="320.0" />
      <attributeLabelStyle>
        <offset x="0.0" y="-0.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>left</textAlignment>
      </attributeLabelStyle>
      <objectLabelStyle>
        <offset x="0.0" y="12.0" />
        <backgroundColor>#ffffffff</backgroundColor>
        <textColor>#ff000000</textColor>
        <textAlignment>center</textAlignment>
      </objectLabelStyle>
      <concept>
        <objectContingent>
          <object>&quot;internalDriveSlots&quot; = 44</object>
        </objectContingent>
        <attributeContingent />
      </concept>
    </node>
    <edge from="1" to="2" />
    <edge from="1" to="5" />
    <edge from="2" to="3" />
    <edge from="2" to="6" />
    <edge from="3" to="4" />
    <edge from="3" to="7" />
    <edge from="4" to="8" />
    <edge from="4" to="17" />
    <edge from="5" to="6" />
    <edge from="5" to="9" />
    <edge from="6" to="7" />
    <edge from="6" to="10" />
    <edge from="11" to="12" />
    <edge from="11" to="15" />
    <edge from="12" to="16" />
    <edge from="12" to="19" />
    <edge from="13" to="14" />
    <edge from="13" to="21" />
    <edge from="14" to="15" />
    <edge from="14" to="22" />
    <edge from="15" to="16" />
    <edge from="15" to="23" />
    <edge from="16" to="20" />
    <edge from="16" to="24" />
    <edge from="17" to="18" />
    <edge from="18" to="19" />
    <edge from="19" to="20" />
    <edge from="20" to="25" />
    <edge from="21" to="22" />
    <edge from="22" to="23" />
    <edge from="23" to="24" />
    <edge from="24" to="25" />
    <edge from="8" to="12" />
    <edge from="8" to="18" />
    <edge from="7" to="8" />
    <edge from="7" to="11" />
    <edge from="10" to="11" />
    <edge from="10" to="14" />
    <edge from="9" to="10" />
    <edge from="9" to="13" />
  </diagram>
</conceptualSchema>

