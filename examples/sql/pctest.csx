<?xml version="1.0" encoding="UTF-8"?>
<!--
This is an example conceptual schema for ToscanaJ.

It is meant to be used together with a database as defined by the SQL script
pctest.sql, which should have been provided along this file. A database based
on this script has to run in a database system that is accessible through
JDBC.

The current setup assumes McKoi is used as database management system. You can
get McKoi from http://www.mckoi.com, it is a GPLed, Java-based database system.
Check that the username and password at the beginning match your setup.

If you want to use another DBMS, you have to change the driver and the url, too.
Refer to the documentation of your DBMS which class and URL scheme to use.
-->
<conceptualSchema version="1.0" askDatabase="true">
    <description>
        <html>
            <head>
                <title>PC-Test</title>
            </head>
            <body>
                <h1>The PC-Test example</h1>
                This is a classic example for using Toscana. It has been ported
                and translated to be a part of the ToscanaJ distribution, the data
                is still based on an old test of 486 PCs from a German computer
                magazine.
            </body>
        </html>
    </description>
	<database>
		<url driver="com.mckoi.JDBCDriver" user="user" password="password">jdbc:mckoi://localhost/</url>
		<table>PCTest</table>
		<key>PCname</key>
		<queries dropDefaults="false">
			<list name="Cases" distinct="true">
				<column>typeCase</column>
			</list>
			<list name="Name with type of case" head="Case: ">
				<column name="Case" separator=" - PC: ">typeCase</column>
				<column name="Name">PCname</column>
			</list>
			<aggregate name="Average Price">
				<column format="$ 0.00">AVG(price)</column>
			</aggregate>
			<aggregate name="Min/Max Prices">
				<column format="$0.00" separator=" - ">MIN(price)</column>
				<column format="$0.00">MAX(price)</column>
			</aggregate>
		</queries>
	</database>
        <viewer class="net.sourceforge.toscanaj.dbviewer.HTMLDatabaseViewer" name="HTML View">
                <parameter name="template" value="/home/pbecker/temp/objectView.html"/>
        </viewer>
        <viewer class="net.sourceforge.toscanaj.dbviewer.SimpleDatabaseViewer" name="Short View">
                <parameter name="openDelimiter" value="$$$"/>
                <parameter name="closeDelimiter" value="%%%"/>
                <template>Name:  $$$PCname%%%
Price: $$$price%%%
                </template>
        </viewer>
        <viewer class="net.sourceforge.toscanaj.dbviewer.ProgramCallDatabaseViewer" name="External View">
                <parameter name="openDelimiter" value="$$$"/>
                <parameter name="closeDelimiter" value="%%%"/>
                <parameter name="commandLine" value="echo $$$PCname%%%"/>
        </viewer>
        <report class="net.sourceforge.toscanaj.dbviewer.HTMLDatabaseReportGenerator" name="HTML Report">
                <parameter name="template" value="/home/pbecker/temp/report.html"/>
        </report>
	<context>
		<object id="1">price&gt;=5000</object>
		<object id="2">price&lt;5000 and price&gt;=4500</object>
		<object id="3">price&lt;4500 and price&gt;=4000</object>
		<object id="4">price&lt;4000 and price&gt;=3500</object>
		<object id="5">price&lt;3500 and price&gt;=3000</object>
		<object id="6">price&lt;3000 and price&gt;=2500</object>
		<object id="7">price&lt;2500</object>
		<object id="8">harddisk&gt;=500</object>
		<object id="9">harddisk&lt;500 and harddisk&gt;=450</object>
		<object id="10">harddisk&lt;450 and harddisk&gt;=400</object>
		<object id="11">harddisk&lt;400 and harddisk&gt;=350</object>
		<object id="12">harddisk&lt;350 and harddisk&gt;=300</object>
		<object id="13">harddisk&lt;250 and harddisk&gt;=225</object>
		<object id="14">harddisk&lt;225</object>
		<object id="11">harddisk&lt;400 and harddisk&gt;=350</object>
		<object id="12">harddisk&lt;350 and harddisk&gt;=300</object>
		<object id="13">harddisk&lt;250 and harddisk&gt;=225</object>
		<object id="14">harddisk&lt;225</object>
		<object id="11">harddisk&lt;400 and harddisk&gt;=350</object>
		<object id="12">harddisk&lt;350 and harddisk&gt;=300</object>
		<object id="13">harddisk&lt;250 and harddisk&gt;=225</object>
		<object id="14">harddisk&lt;225</object>
		<object id="15">diskmark&gt;0 and diskmark&lt;=15 AND graphics&gt;0 and graphics&lt;=5</object>
		<object id="16">diskmark&gt;0 and diskmark&lt;=15 AND graphics&gt;5 and graphics&lt;=10</object>
		<object id="17">diskmark&gt;0 and diskmark&lt;=15 AND graphics&gt;10 and graphics&lt;=15</object>
		<object id="18">diskmark&gt;0 and diskmark&lt;=15 AND graphics&gt;15 and graphics&lt;=20</object>
		<object id="19">diskmark&gt;0 and diskmark&lt;=15 AND graphics&gt;20 and graphics&lt;=30</object>
		<object id="20">diskmark&gt;0 and diskmark&lt;=15 AND graphics&gt;30 and graphics&lt;=40</object>
		<object id="21">diskmark&gt;0 and diskmark&lt;=15 AND graphics&gt;40</object>
		<object id="22">diskmark&gt;15 and diskmark&lt;=30 AND graphics&gt;0 and graphics&lt;=5</object>
		<object id="23">diskmark&gt;30 and diskmark&lt;=45 AND graphics&gt;0 and graphics&lt;=5</object>
		<object id="24">diskmark&gt;45 and diskmark&lt;=60 AND graphics&gt;0 and graphics&lt;=5</object>
		<object id="25">diskmark&gt;60 and diskmark&lt;=90 AND graphics&gt;0 and graphics&lt;=5</object>
		<object id="26">diskmark&gt;90 and diskmark&lt;=120 AND graphics&gt;0 and graphics&lt;=5</object>
		<object id="27">diskmark&gt;120 AND graphics&gt;0 and graphics&lt;=5</object>
		<object id="28">diskmark&gt;15 and diskmark&lt;=30 AND graphics&gt;5 and graphics&lt;=10</object>
		<object id="29">diskmark&gt;15 and diskmark&lt;=30 AND graphics&gt;10 and graphics&lt;=15</object>
		<object id="30">diskmark&gt;15 and diskmark&lt;=30 AND graphics&gt;15 and graphics&lt;=20</object>
		<object id="31">diskmark&gt;15 and diskmark&lt;=30 AND graphics&gt;20 and graphics&lt;=30</object>
		<object id="32">diskmark&gt;15 and diskmark&lt;=30 AND graphics&gt;30 and graphics&lt;=40</object>
		<object id="33">diskmark&gt;15 and diskmark&lt;=30 AND graphics&gt;40</object>
		<object id="34">diskmark&gt;30 and diskmark&lt;=45 AND graphics&gt;5 and graphics&lt;=10</object>
		<object id="35">diskmark&gt;45 and diskmark&lt;=60 AND graphics&gt;5 and graphics&lt;=10</object>
		<object id="36">diskmark&gt;60 and diskmark&lt;=90 AND graphics&gt;5 and graphics&lt;=10</object>
		<object id="37">diskmark&gt;90 and diskmark&lt;=120 AND graphics&gt;5 and graphics&lt;=10</object>
		<object id="38">diskmark&gt;120 AND graphics&gt;5 and graphics&lt;=10</object>
		<object id="39">diskmark&gt;30 and diskmark&lt;=45 AND graphics&gt;10 and graphics&lt;=15</object>
		<object id="40">diskmark&gt;30 and diskmark&lt;=45 AND graphics&gt;15 and graphics&lt;=20</object>
		<object id="41">diskmark&gt;30 and diskmark&lt;=45 AND graphics&gt;20 and graphics&lt;=30</object>
		<object id="42">diskmark&gt;30 and diskmark&lt;=45 AND graphics&gt;30 and graphics&lt;=40</object>
		<object id="43">diskmark&gt;30 and diskmark&lt;=45 AND graphics&gt;40</object>
		<object id="44">diskmark&gt;45 and diskmark&lt;=60 AND graphics&gt;10 and graphics&lt;=15</object>
		<object id="45">diskmark&gt;60 and diskmark&lt;=90 AND graphics&gt;10 and graphics&lt;=15</object>
		<object id="46">diskmark&gt;90 and diskmark&lt;=120 AND graphics&gt;10 and graphics&lt;=15</object>
		<object id="47">diskmark&gt;120 AND graphics&gt;10 and graphics&lt;=15</object>
		<object id="48">diskmark&gt;45 and diskmark&lt;=60 AND graphics&gt;15 and graphics&lt;=20</object>
		<object id="49">diskmark&gt;45 and diskmark&lt;=60 AND graphics&gt;20 and graphics&lt;=30</object>
		<object id="50">diskmark&gt;45 and diskmark&lt;=60 AND graphics&gt;30 and graphics&lt;=40</object>
		<object id="51">diskmark&gt;45 and diskmark&lt;=60 AND graphics&gt;40</object>
		<object id="52">diskmark&gt;60 and diskmark&lt;=90 AND graphics&gt;15 and graphics&lt;=20</object>
		<object id="53">diskmark&gt;90 and diskmark&lt;=120 AND graphics&gt;15 and graphics&lt;=20</object>
		<object id="54">diskmark&gt;120 AND graphics&gt;15 and graphics&lt;=20</object>
		<object id="55">diskmark&gt;60 and diskmark&lt;=90 AND graphics&gt;20 and graphics&lt;=30</object>
		<object id="56">diskmark&gt;60 and diskmark&lt;=90 AND graphics&gt;30 and graphics&lt;=40</object>
		<object id="57">diskmark&gt;60 and diskmark&lt;=90 AND graphics&gt;40</object>
		<object id="58">diskmark&gt;90 and diskmark&lt;=120 AND graphics&gt;20 and graphics&lt;=30</object>
		<object id="59">diskmark&gt;120 AND graphics&gt;20 and graphics&lt;=30</object>
		<object id="60">diskmark&gt;90 and diskmark&lt;=120 AND graphics&gt;30 and graphics&lt;=40</object>
		<object id="61">diskmark&gt;90 and diskmark&lt;=120 AND graphics&gt;40</object>
		<object id="62">diskmark&gt;120 AND graphics&gt;30 and graphics&lt;=40</object>
		<object id="63">diskmark&gt;120 AND graphics&gt;40</object>
		<object id="64"/>
		<object id="65">typeBus='ISA'</object>
		<object id="66">typeBus='EISA'</object>
		<object id="67">typeBus='MCA'</object>
		<object id="68">harddisk &lt; 200</object>
		<object id="69">harddisk Between 200 And 399</object>
		<object id="70">harddisk &gt;= 400</object>
		<object id="71">video&gt;=9000</object>
		<object id="72">video between 7500 and 8999</object>
		<object id="73">video between 6000 and 7499</object>
		<object id="74">video between 4500 and 5999</object>
		<object id="75">video between 3000 and 4499</object>
		<object id="76">video between 1500 and 2999</object>
		<object id="77">video&lt;1500</object>
		<object id="78">diskmark&gt;0 and diskmark&lt;=15 AND dosmark&gt;=0 and dosmark&lt;40</object>
		<object id="79">diskmark&gt;0 and diskmark&lt;=15 AND dosmark&gt;=40 and dosmark&lt;50</object>
		<object id="80">diskmark&gt;0 and diskmark&lt;=15 AND dosmark&gt;=50 and dosmark&lt;60</object>
		<object id="81">diskmark&gt;0 and diskmark&lt;=15 AND dosmark&gt;=60 and dosmark&lt;70</object>
		<object id="82">diskmark&gt;0 and diskmark&lt;=15 AND dosmark&gt;=70 and dosmark&lt;80</object>
		<object id="83">diskmark&gt;0 and diskmark&lt;=15 AND dosmark&gt;=80 and dosmark&lt;90</object>
		<object id="84">diskmark&gt;0 and diskmark&lt;=15 AND dosmark&gt;=90</object>
		<object id="85">diskmark&gt;15 and diskmark&lt;=30 AND dosmark&gt;=0 and dosmark&lt;40</object>
		<object id="86">diskmark&gt;30 and diskmark&lt;=45 AND dosmark&gt;=0 and dosmark&lt;40</object>
		<object id="87">diskmark&gt;45 and diskmark&lt;=60 AND dosmark&gt;=0 and dosmark&lt;40</object>
		<object id="88">diskmark&gt;60 and diskmark&lt;=90 AND dosmark&gt;=0 and dosmark&lt;40</object>
		<object id="89">diskmark&gt;90 and diskmark&lt;=120 AND dosmark&gt;=0 and dosmark&lt;40</object>
		<object id="90">diskmark&gt;120 AND dosmark&gt;=0 and dosmark&lt;40</object>
		<object id="91">diskmark&gt;15 and diskmark&lt;=30 AND dosmark&gt;=40 and dosmark&lt;50</object>
		<object id="92">diskmark&gt;15 and diskmark&lt;=30 AND dosmark&gt;=50 and dosmark&lt;60</object>
		<object id="93">diskmark&gt;15 and diskmark&lt;=30 AND dosmark&gt;=60 and dosmark&lt;70</object>
		<object id="94">diskmark&gt;15 and diskmark&lt;=30 AND dosmark&gt;=70 and dosmark&lt;80</object>
		<object id="95">diskmark&gt;15 and diskmark&lt;=30 AND dosmark&gt;=80 and dosmark&lt;90</object>
		<object id="96">diskmark&gt;15 and diskmark&lt;=30 AND dosmark&gt;=90</object>
		<object id="97">diskmark&gt;30 and diskmark&lt;=45 AND dosmark&gt;=40 and dosmark&lt;50</object>
		<object id="98">diskmark&gt;45 and diskmark&lt;=60 AND dosmark&gt;=40 and dosmark&lt;50</object>
		<object id="99">diskmark&gt;60 and diskmark&lt;=90 AND dosmark&gt;=40 and dosmark&lt;50</object>
		<object id="100">diskmark&gt;90 and diskmark&lt;=120 AND dosmark&gt;=40 and dosmark&lt;50</object>
		<object id="101">diskmark&gt;120 AND dosmark&gt;=40 and dosmark&lt;50</object>
		<object id="102">diskmark&gt;30 and diskmark&lt;=45 AND dosmark&gt;=50 and dosmark&lt;60</object>
		<object id="103">diskmark&gt;30 and diskmark&lt;=45 AND dosmark&gt;=60 and dosmark&lt;70</object>
		<object id="104">diskmark&gt;30 and diskmark&lt;=45 AND dosmark&gt;=70 and dosmark&lt;80</object>
		<object id="105">diskmark&gt;30 and diskmark&lt;=45 AND dosmark&gt;=80 and dosmark&lt;90</object>
		<object id="106">diskmark&gt;30 and diskmark&lt;=45 AND dosmark&gt;=90</object>
		<object id="107">diskmark&gt;45 and diskmark&lt;=60 AND dosmark&gt;=50 and dosmark&lt;60</object>
		<object id="108">diskmark&gt;60 and diskmark&lt;=90 AND dosmark&gt;=50 and dosmark&lt;60</object>
		<object id="109">diskmark&gt;90 and diskmark&lt;=120 AND dosmark&gt;=50 and dosmark&lt;60</object>
		<object id="110">diskmark&gt;120 AND dosmark&gt;=50 and dosmark&lt;60</object>
		<object id="111">diskmark&gt;45 and diskmark&lt;=60 AND dosmark&gt;=60 and dosmark&lt;70</object>
		<object id="112">diskmark&gt;45 and diskmark&lt;=60 AND dosmark&gt;=70 and dosmark&lt;80</object>
		<object id="113">diskmark&gt;45 and diskmark&lt;=60 AND dosmark&gt;=80 and dosmark&lt;90</object>
		<object id="114">diskmark&gt;45 and diskmark&lt;=60 AND dosmark&gt;=90</object>
		<object id="115">diskmark&gt;60 and diskmark&lt;=90 AND dosmark&gt;=60 and dosmark&lt;70</object>
		<object id="116">diskmark&gt;90 and diskmark&lt;=120 AND dosmark&gt;=60 and dosmark&lt;70</object>
		<object id="117">diskmark&gt;120 AND dosmark&gt;=60 and dosmark&lt;70</object>
		<object id="118">diskmark&gt;60 and diskmark&lt;=90 AND dosmark&gt;=70 and dosmark&lt;80</object>
		<object id="119">diskmark&gt;60 and diskmark&lt;=90 AND dosmark&gt;=80 and dosmark&lt;90</object>
		<object id="120">diskmark&gt;60 and diskmark&lt;=90 AND dosmark&gt;=90</object>
		<object id="121">diskmark&gt;90 and diskmark&lt;=120 AND dosmark&gt;=70 and dosmark&lt;80</object>
		<object id="122">diskmark&gt;120 AND dosmark&gt;=70 and dosmark&lt;80</object>
		<object id="123">diskmark&gt;90 and diskmark&lt;=120 AND dosmark&gt;=80 and dosmark&lt;90</object>
		<object id="124">diskmark&gt;90 and diskmark&lt;=120 AND dosmark&gt;=90</object>
		<object id="125">diskmark&gt;120 AND dosmark&gt;=80 and dosmark&lt;90</object>
		<object id="126">diskmark&gt;120 AND dosmark&gt;=90</object>
		<object id="127">directsales=1 and dealer=0</object>
		<object id="128">directsales=0 and dealer=1</object>
		<object id="129">directsales=1 and dealer=1</object>
		<object id="130">typeCase = 'Desktop'</object>
		<object id="131">typeCase = 'Slimline'</object>
		<object id="132">typeCase = 'Small-footprint'</object>
		<object id="133">typeCase = 'Tower'</object>
		<object id="134">typeCase = 'Minitower'</object>
		<object id="135">freeDriveSlots = 0</object>
		<object id="136">freeDriveSlots = 1</object>
		<object id="137">freeDriveSlots = 2</object>
		<object id="138">freeDriveSlots = 3</object>
		<object id="139">freeDriveSlots = 10</object>
		<object id="140">freeDriveSlots = 11</object>
		<object id="141">freeDriveSlots = 12</object>
		<object id="142">freeDriveSlots = 13</object>
		<object id="143">freeDriveSlots = 20</object>
		<object id="144">freeDriveSlots = 21</object>
		<object id="145">freeDriveSlots = 22</object>
		<object id="146">freeDriveSlots = 23</object>
		<object id="147">freeDriveSlots = 30</object>
		<object id="148">freeDriveSlots = 31</object>
		<object id="149">freeDriveSlots = 32</object>
		<object id="150">freeDriveSlots = 33</object>
		<object id="151">freeDriveSlots = 40</object>
		<object id="152">freeDriveSlots = 41</object>
		<object id="153">freeDriveSlots = 42</object>
		<object id="154">freeDriveSlots = 43</object>
		<object id="155">freeDriveSlots = 50</object>
		<object id="156">freeDriveSlots = 51</object>
		<object id="157">freeDriveSlots = 52</object>
		<object id="158">freeDriveSlots = 53</object>
		<object id="159">freeDriveSlots = 60</object>
		<object id="160">freeDriveSlots = 61</object>
		<object id="161">freeDriveSlots = 62</object>
		<object id="162">freeDriveSlots = 63</object>
		<object id="163">freeDriveSlots = 70</object>
		<object id="164">freeDriveSlots = 71</object>
		<object id="165">freeDriveSlots = 72</object>
		<object id="166">freeDriveSlots = 73</object>
		<object id="167">powerSupply&lt;150 and powerConnectors=5</object>
		<object id="168">powerSupply between 150 and 199 and powerConnectors=5</object>
		<object id="169">powerSupply between 200 and 249 and powerConnectors=5</object>
		<object id="170">powerSupply between 250 and 299 and powerConnectors=5</object>
		<object id="171">powerSupply&gt;=300 and powerConnectors=5</object>
		<object id="172">powerSupply&lt;150 and powerConnectors=2</object>
		<object id="173">powerSupply&lt;150 and powerConnectors=6</object>
		<object id="174">powerSupply between 150 and 199 and powerConnectors=6</object>
		<object id="175">powerSupply between 200 and 249 and powerConnectors=6</object>
		<object id="176">powerSupply between 250 and 299 and powerConnectors=6</object>
		<object id="177">powerSupply&gt;=300 and powerConnectors=6</object>
		<object id="178">powerSupply&lt;150 and powerConnectors=7</object>
		<object id="179">powerSupply between 150 and 199 and powerConnectors=7</object>
		<object id="180">powerSupply between 200 and 249 and powerConnectors=7</object>
		<object id="181">powerSupply between 250 and 299 and powerConnectors=7</object>
		<object id="182">powerSupply&gt;=300 and powerConnectors=7</object>
		<object id="183">powerSupply&lt;150 and powerConnectors=8</object>
		<object id="184">powerSupply between 150 and 199 and powerConnectors=8</object>
		<object id="185">powerSupply between 200 and 249 and powerConnectors=8</object>
		<object id="186">powerSupply between 250 and 299 and powerConnectors=8</object>
		<object id="187">powerSupply&gt;=300 and powerConnectors=8</object>
		<object id="188">powerSupply&lt;150 and powerConnectors=4</object>
		<object id="189">powerSupply between 150 and 199 and powerConnectors=4</object>
		<object id="190">powerSupply between 200 and 249 and powerConnectors=4</object>
		<object id="191">powerSupply between 250 and 299 and powerConnectors=4</object>
		<object id="192">powerSupply&gt;=300 and powerConnectors=4</object>
		<object id="193">powerSupply&lt;150 and powerConnectors=3</object>
		<object id="194">powerSupply between 150 and 199 and powerConnectors=3</object>
		<object id="195">powerSupply between 200 and 249 and powerConnectors=3</object>
		<object id="196">powerSupply between 250 and 299 and powerConnectors=3</object>
		<object id="197">powerSupply&gt;=300 and powerConnectors=3</object>
		<object id="198">powerSupply between 150 and 199 and powerConnectors=2</object>
		<object id="199">powerSupply between 200 and 249 and powerConnectors=2</object>
		<object id="200">powerSupply between 250 and 299 and powerConnectors=2</object>
		<object id="201">powerSupply&gt;=300 and powerConnectors=2</object>
		<object id="202">graphiccard = 'ISA'</object>
		<object id="203">graphiccard = 'EISA'</object>
		<object id="204">graphiccard = 'Local Bus'</object>
		<object id="205">graphiccard = 'Motherboard'</object>
		<object id="206">graphiccard = 'MCA'</object>
		<object id="207">graphiccard = 'VESA Local Bus'</object>
		<object id="208">graphiccard = 'Proprietary Local Bus'</object>
		<object id="209">graphiccard = 'UBSA Local Bus'</object>
		<object id="210">ports = 'Keine'</object>
		<object id="211">ports = '1,1,0'</object>
		<object id="212">ports = '1,1,1'</object>
		<object id="213">ports = '1,2,0'</object>
		<object id="214">ports = '1,2,1'</object>
		<object id="215">ports = '2,1,0'</object>
		<object id="216">ports = '2,1,1'</object>
		<object id="217">internalDriveSlots = 0</object>
		<object id="218">internalDriveSlots = 10</object>
		<object id="219">internalDriveSlots = 20</object>
		<object id="220">internalDriveSlots = 30</object>
		<object id="221">internalDriveSlots = 1</object>
		<object id="222">internalDriveSlots = 11</object>
		<object id="223">internalDriveSlots = 21</object>
		<object id="224">internalDriveSlots = 31</object>
		<object id="225">internalDriveSlots = 2</object>
		<object id="226">internalDriveSlots = 12</object>
		<object id="227">internalDriveSlots = 22</object>
		<object id="228">internalDriveSlots = 32</object>
		<object id="229">internalDriveSlots = 3</object>
		<object id="230">internalDriveSlots = 13</object>
		<object id="231">internalDriveSlots = 23</object>
		<object id="232">internalDriveSlots = 33</object>
		<object id="233">internalDriveSlots = 40</object>
		<object id="234">internalDriveSlots = 41</object>
		<object id="235">internalDriveSlots = 42</object>
		<object id="236">internalDriveSlots = 43</object>
		<object id="237">internalDriveSlots = 4</object>
		<object id="238">internalDriveSlots = 14</object>
		<object id="239">internalDriveSlots = 24</object>
		<object id="240">internalDriveSlots = 34</object>
		<object id="241">internalDriveSlots = 44</object>
		<attribute id="1"/>
		<attribute id="2"/>
		<attribute id="3">&gt;=2500$</attribute>
		<attribute id="4">&gt;=3000$</attribute>
		<attribute id="5">&gt;=3500$</attribute>
		<attribute id="6">&gt;=4000$</attribute>
		<attribute id="7">&gt;=4500$</attribute>
		<attribute id="8">&gt;=5000$</attribute>
		<attribute id="9">&lt;5000$</attribute>
		<attribute id="10">&lt;4500$</attribute>
		<attribute id="11">&lt;4000$</attribute>
		<attribute id="12">&lt;3500$</attribute>
		<attribute id="13">&lt;3000$</attribute>
		<attribute id="14">&lt;2500$</attribute>
		<attribute id="15"/>
		<attribute id="16"/>
		<attribute id="17">&gt;=225MB</attribute>
		<attribute id="18">&gt;=250MB</attribute>
		<attribute id="19">&gt;=350MB</attribute>
		<attribute id="20">&gt;=400MB</attribute>
		<attribute id="21">&gt;=450MB</attribute>
		<attribute id="22">&gt;=500MB</attribute>
		<attribute id="23">&lt;500MB</attribute>
		<attribute id="24">&lt;450MB</attribute>
		<attribute id="25">&lt;400MB</attribute>
		<attribute id="26">&lt;350MB</attribute>
		<attribute id="27">&lt;250MB</attribute>
		<attribute id="28">&lt;225MB</attribute>
		<attribute id="29">Disk WinMark &gt; 0, Graphics WinMark &gt; 0</attribute>
		<attribute id="30">&gt; 5</attribute>
		<attribute id="31">&gt; 10</attribute>
		<attribute id="32">&gt; 15</attribute>
		<attribute id="33">&gt; 20</attribute>
		<attribute id="34">&gt; 30</attribute>
		<attribute id="35">&gt; 40</attribute>
		<attribute id="36">&gt; 15</attribute>
		<attribute id="37">&gt; 30</attribute>
		<attribute id="38">&gt; 45</attribute>
		<attribute id="39">&gt; 60</attribute>
		<attribute id="40">&gt; 90</attribute>
		<attribute id="41">&gt; 120</attribute>
		<attribute id="42">ISA-Bus</attribute>
		<attribute id="43">EISA-Bus</attribute>
		<attribute id="44">MCA-Bus</attribute>
		<attribute id="45">&gt;=200MB</attribute>
		<attribute id="46">&gt;=400MB</attribute>
		<attribute id="47"/>
		<attribute id="48"/>
		<attribute id="49">&gt;=1500</attribute>
		<attribute id="50">&gt;=3000</attribute>
		<attribute id="51">&gt;=4500</attribute>
		<attribute id="52">&gt;=6000</attribute>
		<attribute id="53">&gt;=7500</attribute>
		<attribute id="54">&gt;=9000</attribute>
		<attribute id="55">&lt;9000</attribute>
		<attribute id="56">&lt;7500</attribute>
		<attribute id="57">&lt;6000</attribute>
		<attribute id="58">&lt;4500</attribute>
		<attribute id="59">&lt;3000</attribute>
		<attribute id="60">&lt;1500</attribute>
		<attribute id="61">Disk WinMark &gt; 0, DOSmark &gt;= 0</attribute>
		<attribute id="62">&gt;= 40</attribute>
		<attribute id="63">&gt;= 50</attribute>
		<attribute id="64">&gt;= 60</attribute>
		<attribute id="65">&gt;= 70</attribute>
		<attribute id="66">&gt;= 80</attribute>
		<attribute id="67">&gt;= 90</attribute>
		<attribute id="68">&gt; 15</attribute>
		<attribute id="69">&gt; 30</attribute>
		<attribute id="70">&gt; 45</attribute>
		<attribute id="71">&gt; 60</attribute>
		<attribute id="72">&gt; 90</attribute>
		<attribute id="73">&gt; 120</attribute>
		<attribute id="74">direct sales</attribute>
		<attribute id="75">only direct sales</attribute>
		<attribute id="76">shops</attribute>
		<attribute id="77">only shops</attribute>
		<attribute id="78">both forms</attribute>
		<attribute id="79">Desktop</attribute>
		<attribute id="80">Slimline</attribute>
		<attribute id="81">Small-footprint</attribute>
		<attribute id="82">Tower</attribute>
		<attribute id="83">Mini-Tower</attribute>
		<attribute id="84">no 5¼" bay</attribute>
		<attribute id="85">no 3½" bay</attribute>
		<attribute id="86">one 3½" bay</attribute>
		<attribute id="87">two 3½" bays</attribute>
		<attribute id="88">three 3½" bays</attribute>
		<attribute id="89">one 5¼" bay</attribute>
		<attribute id="90">two 5¼" bays</attribute>
		<attribute id="91">three 5¼" bays</attribute>
		<attribute id="92">four 5¼" bays</attribute>
		<attribute id="93">five 5¼" bays</attribute>
		<attribute id="94">six 5¼" bays</attribute>
		<attribute id="95">seven 5¼" bays</attribute>
		<attribute id="96">5 connectors</attribute>
		<attribute id="97"/>
		<attribute id="98"/>
		<attribute id="99"/>
		<attribute id="100"/>
		<attribute id="101">power &lt;150 Watt</attribute>
		<attribute id="102">2 connectors</attribute>
		<attribute id="103">6 connectors</attribute>
		<attribute id="104"/>
		<attribute id="105"/>
		<attribute id="106"/>
		<attribute id="107"/>
		<attribute id="108">7 connectors</attribute>
		<attribute id="109"/>
		<attribute id="110"/>
		<attribute id="111"/>
		<attribute id="112"/>
		<attribute id="113">8 connectors</attribute>
		<attribute id="114"/>
		<attribute id="115"/>
		<attribute id="116"/>
		<attribute id="117"/>
		<attribute id="118">4 connectors</attribute>
		<attribute id="119"/>
		<attribute id="120"/>
		<attribute id="121"/>
		<attribute id="122"/>
		<attribute id="123">3 connectors</attribute>
		<attribute id="124"/>
		<attribute id="125"/>
		<attribute id="126"/>
		<attribute id="127"/>
		<attribute id="128">&lt;200 Watt</attribute>
		<attribute id="129">&lt;250 Watt</attribute>
		<attribute id="130">&lt;300 Watt</attribute>
		<attribute id="131">&gt;=300 Watt</attribute>
		<attribute id="132">ISA Bus</attribute>
		<attribute id="133">EISA Bus</attribute>
		<attribute id="134">Local Bus</attribute>
		<attribute id="135">Motherboard</attribute>
		<attribute id="136">MCA</attribute>
		<attribute id="137">VESA Local Bus</attribute>
		<attribute id="138">Proprietary Local Bus</attribute>
		<attribute id="139">UBSA Local BUS</attribute>
		<attribute id="140">one parallel port</attribute>
		<attribute id="141">one serial port</attribute>
		<attribute id="142">one mouse-port</attribute>
		<attribute id="143">two serial ports</attribute>
		<attribute id="144">two parallel ports</attribute>
		<attribute id="145">no 5¼" bay</attribute>
		<attribute id="146">no 3½" bay</attribute>
		<attribute id="147">one 5½" bay</attribute>
		<attribute id="148">two 5½" bays</attribute>
		<attribute id="149">three 5½" bays</attribute>
		<attribute id="150">one 3¼" bay</attribute>
		<attribute id="151">two 3¼" bays</attribute>
		<attribute id="152">three 3¼" bays</attribute>
		<attribute id="153">four 5½" bays</attribute>
		<attribute id="154">four 3¼" bays</attribute>
	</context>
	<diagram title="Prices for 486/66 PCs">
		<concept id="1">
			<position x="0.000000" y="0.000000"/>
			<objectContingent/>
			<attributeContingent>
				<labelStyle>
					<offset x="-3.000000" y="3.000000"/>
					<textAlignment>right</textAlignment>
				</labelStyle>
				<attributeRef>1</attributeRef>
				<attributeRef>2</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="2">
			<position x="20.000000" y="-20.000000"/>
			<objectContingent/>
			<attributeContingent>
				<labelStyle>
					<offset x="3.000000" y="3.000000"/>
					<textAlignment>left</textAlignment>
				</labelStyle>
				<attributeRef>3</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="3">
			<position x="40.000000" y="-40.000000"/>
			<objectContingent/>
			<attributeContingent>
				<labelStyle>
					<offset x="3.000000" y="3.000000"/>
					<textAlignment>left</textAlignment>
				</labelStyle>
				<attributeRef>4</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="4">
			<position x="60.000000" y="-60.000000"/>
			<objectContingent/>
			<attributeContingent>
				<labelStyle>
					<offset x="3.000000" y="3.000000"/>
					<textAlignment>left</textAlignment>
				</labelStyle>
				<attributeRef>5</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="5">
			<position x="80.000000" y="-80.000000"/>
			<objectContingent/>
			<attributeContingent>
				<labelStyle>
					<offset x="3.000000" y="3.000000"/>
					<textAlignment>left</textAlignment>
				</labelStyle>
				<attributeRef>6</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="6">
			<position x="100.000000" y="-100.000000"/>
			<objectContingent/>
			<attributeContingent>
				<labelStyle>
					<offset x="3.000000" y="3.000000"/>
					<textAlignment>left</textAlignment>
				</labelStyle>
				<attributeRef>7</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="7">
			<position x="120.000000" y="-120.000000"/>
			<objectContingent>
				<labelStyle>
					<offset x="0.000000" y="-4.000000"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>1</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="3.000000" y="3.000000"/>
					<textAlignment>left</textAlignment>
				</labelStyle>
				<attributeRef>8</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="8">
			<position x="-20.000000" y="-20.000000"/>
			<objectContingent/>
			<attributeContingent>
				<labelStyle>
					<offset x="-3.000000" y="3.000000"/>
					<textAlignment>right</textAlignment>
				</labelStyle>
				<attributeRef>9</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="9">
			<position x="0.000000" y="-40.000000"/>
			<objectContingent/>
			<attributeContingent/>
		</concept>
		<concept id="10">
			<position x="20.000000" y="-60.000000"/>
			<objectContingent/>
			<attributeContingent/>
		</concept>
		<concept id="11">
			<position x="40.000000" y="-80.000000"/>
			<objectContingent/>
			<attributeContingent/>
		</concept>
		<concept id="12">
			<position x="60.000000" y="-100.000000"/>
			<objectContingent/>
			<attributeContingent/>
		</concept>
		<concept id="13">
			<position x="80.000000" y="-120.000000"/>
			<objectContingent>
				<labelStyle>
					<offset x="0.000000" y="-4.000000"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>2</objectRef>
			</objectContingent>
			<attributeContingent/>
		</concept>
		<concept id="14">
			<position x="-40.000000" y="-40.000000"/>
			<objectContingent/>
			<attributeContingent>
				<labelStyle>
					<offset x="-3.000000" y="3.000000"/>
					<textAlignment>right</textAlignment>
				</labelStyle>
				<attributeRef>10</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="15">
			<position x="-20.000000" y="-60.000000"/>
			<objectContingent/>
			<attributeContingent/>
		</concept>
		<concept id="16">
			<position x="0.000000" y="-80.000000"/>
			<objectContingent/>
			<attributeContingent/>
		</concept>
		<concept id="17">
			<position x="20.000000" y="-100.000000"/>
			<objectContingent/>
			<attributeContingent/>
		</concept>
		<concept id="18">
			<position x="40.000000" y="-120.000000"/>
			<objectContingent>
				<labelStyle>
					<offset x="0.000000" y="-4.000000"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>3</objectRef>
			</objectContingent>
			<attributeContingent/>
		</concept>
		<concept id="19">
			<position x="-60.000000" y="-60.000000"/>
			<objectContingent/>
			<attributeContingent>
				<labelStyle>
					<offset x="-3.000000" y="3.000000"/>
					<textAlignment>right</textAlignment>
				</labelStyle>
				<attributeRef>11</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="20">
			<position x="-40.000000" y="-80.000000"/>
			<objectContingent/>
			<attributeContingent/>
		</concept>
		<concept id="21">
			<position x="-20.000000" y="-100.000000"/>
			<objectContingent/>
			<attributeContingent/>
		</concept>
		<concept id="22">
			<position x="0.000000" y="-120.000000"/>
			<objectContingent>
				<labelStyle>
					<offset x="0.000000" y="-4.000000"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>4</objectRef>
			</objectContingent>
			<attributeContingent/>
		</concept>
		<concept id="23">
			<position x="-80.000000" y="-80.000000"/>
			<objectContingent/>
			<attributeContingent>
				<labelStyle>
					<offset x="-3.000000" y="3.000000"/>
					<textAlignment>right</textAlignment>
				</labelStyle>
				<attributeRef>12</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="24">
			<position x="-60.000000" y="-100.000000"/>
			<objectContingent/>
			<attributeContingent/>
		</concept>
		<concept id="25">
			<position x="-40.000000" y="-120.000000"/>
			<objectContingent>
				<labelStyle>
					<offset x="0.000000" y="-4.000000"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>5</objectRef>
			</objectContingent>
			<attributeContingent/>
		</concept>
		<concept id="26">
			<position x="-100.000000" y="-100.000000"/>
			<objectContingent/>
			<attributeContingent>
				<labelStyle>
					<offset x="-3.000000" y="3.000000"/>
					<textAlignment>right</textAlignment>
				</labelStyle>
				<attributeRef>13</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="27">
			<position x="-80.000000" y="-120.000000"/>
			<objectContingent>
				<labelStyle>
					<offset x="0.000000" y="-4.000000"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>6</objectRef>
			</objectContingent>
			<attributeContingent/>
		</concept>
		<concept id="28">
			<position x="-120.000000" y="-120.000000"/>
			<objectContingent>
				<labelStyle>
					<offset x="0.000000" y="-4.000000"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>7</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="-3.000000" y="3.000000"/>
					<textAlignment>right</textAlignment>
				</labelStyle>
				<attributeRef>14</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="29">
			<position x="0.000000" y="-160.000000"/>
			<objectContingent/>
			<attributeContingent/>
		</concept>
		<edge from="1" to="2"/>
		<edge from="1" to="8"/>
		<edge from="2" to="3"/>
		<edge from="2" to="9"/>
		<edge from="3" to="4"/>
		<edge from="3" to="10"/>
		<edge from="4" to="5"/>
		<edge from="4" to="11"/>
		<edge from="5" to="6"/>
		<edge from="5" to="12"/>
		<edge from="6" to="7"/>
		<edge from="6" to="13"/>
		<edge from="7" to="29"/>
		<edge from="8" to="9"/>
		<edge from="8" to="14"/>
		<edge from="9" to="10"/>
		<edge from="9" to="15"/>
		<edge from="10" to="11"/>
		<edge from="10" to="16"/>
		<edge from="11" to="12"/>
		<edge from="11" to="17"/>
		<edge from="12" to="13"/>
		<edge from="12" to="18"/>
		<edge from="13" to="29"/>
		<edge from="14" to="15"/>
		<edge from="14" to="19"/>
		<edge from="15" to="16"/>
		<edge from="15" to="20"/>
		<edge from="16" to="17"/>
		<edge from="16" to="21"/>
		<edge from="17" to="18"/>
		<edge from="17" to="22"/>
		<edge from="18" to="29"/>
		<edge from="19" to="20"/>
		<edge from="19" to="23"/>
		<edge from="20" to="21"/>
		<edge from="20" to="24"/>
		<edge from="21" to="22"/>
		<edge from="21" to="25"/>
		<edge from="22" to="29"/>
		<edge from="23" to="24"/>
		<edge from="23" to="26"/>
		<edge from="24" to="25"/>
		<edge from="24" to="27"/>
		<edge from="25" to="29"/>
		<edge from="26" to="27"/>
		<edge from="26" to="28"/>
		<edge from="27" to="29"/>
		<edge from="28" to="29"/>
	</diagram>
	<diagram title="Sizes of harddisks of 486/66 PCs">
		<concept id="1">
			<position x="0.000000" y="0.000000"/>
			<objectContingent/>
			<attributeContingent>
				<labelStyle>
					<offset x="-3.000000" y="3.000000"/>
					<textAlignment>right</textAlignment>
				</labelStyle>
				<attributeRef>15</attributeRef>
				<attributeRef>16</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="2">
			<position x="20.000000" y="-20.000000"/>
			<objectContingent/>
			<attributeContingent>
				<labelStyle>
					<offset x="3.000000" y="3.000000"/>
					<textAlignment>left</textAlignment>
				</labelStyle>
				<attributeRef>17</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="3">
			<position x="40.000000" y="-40.000000"/>
			<objectContingent/>
			<attributeContingent>
				<labelStyle>
					<offset x="3.000000" y="3.000000"/>
					<textAlignment>left</textAlignment>
				</labelStyle>
				<attributeRef>18</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="4">
			<position x="60.000000" y="-60.000000"/>
			<objectContingent/>
			<attributeContingent>
				<labelStyle>
					<offset x="3.000000" y="3.000000"/>
					<textAlignment>left</textAlignment>
				</labelStyle>
				<attributeRef>19</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="5">
			<position x="80.000000" y="-80.000000"/>
			<objectContingent/>
			<attributeContingent>
				<labelStyle>
					<offset x="3.000000" y="3.000000"/>
					<textAlignment>left</textAlignment>
				</labelStyle>
				<attributeRef>20</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="6">
			<position x="100.000000" y="-100.000000"/>
			<objectContingent/>
			<attributeContingent>
				<labelStyle>
					<offset x="3.000000" y="3.000000"/>
					<textAlignment>left</textAlignment>
				</labelStyle>
				<attributeRef>21</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="7">
			<position x="120.000000" y="-120.000000"/>
			<objectContingent>
				<labelStyle>
					<offset x="0.000000" y="-4.000000"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>8</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="3.000000" y="3.000000"/>
					<textAlignment>left</textAlignment>
				</labelStyle>
				<attributeRef>22</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="8">
			<position x="-20.000000" y="-20.000000"/>
			<objectContingent/>
			<attributeContingent>
				<labelStyle>
					<offset x="-3.000000" y="3.000000"/>
					<textAlignment>right</textAlignment>
				</labelStyle>
				<attributeRef>23</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="9">
			<position x="0.000000" y="-40.000000"/>
			<objectContingent/>
			<attributeContingent/>
		</concept>
		<concept id="10">
			<position x="20.000000" y="-60.000000"/>
			<objectContingent/>
			<attributeContingent/>
		</concept>
		<concept id="11">
			<position x="40.000000" y="-80.000000"/>
			<objectContingent/>
			<attributeContingent/>
		</concept>
		<concept id="12">
			<position x="60.000000" y="-100.000000"/>
			<objectContingent/>
			<attributeContingent/>
		</concept>
		<concept id="13">
			<position x="80.000000" y="-120.000000"/>
			<objectContingent>
				<labelStyle>
					<offset x="0.000000" y="-4.000000"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>9</objectRef>
			</objectContingent>
			<attributeContingent/>
		</concept>
		<concept id="14">
			<position x="-40.000000" y="-40.000000"/>
			<objectContingent/>
			<attributeContingent>
				<labelStyle>
					<offset x="-3.000000" y="3.000000"/>
					<textAlignment>right</textAlignment>
				</labelStyle>
				<attributeRef>24</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="15">
			<position x="-20.000000" y="-60.000000"/>
			<objectContingent/>
			<attributeContingent/>
		</concept>
		<concept id="16">
			<position x="0.000000" y="-80.000000"/>
			<objectContingent/>
			<attributeContingent/>
		</concept>
		<concept id="17">
			<position x="20.000000" y="-100.000000"/>
			<objectContingent/>
			<attributeContingent/>
		</concept>
		<concept id="18">
			<position x="40.000000" y="-120.000000"/>
			<objectContingent>
				<labelStyle>
					<offset x="0.000000" y="-4.000000"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>10</objectRef>
			</objectContingent>
			<attributeContingent/>
		</concept>
		<concept id="19">
			<position x="-60.000000" y="-60.000000"/>
			<objectContingent/>
			<attributeContingent>
				<labelStyle>
					<offset x="-3.000000" y="3.000000"/>
					<textAlignment>right</textAlignment>
				</labelStyle>
				<attributeRef>25</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="20">
			<position x="-40.000000" y="-80.000000"/>
			<objectContingent/>
			<attributeContingent/>
		</concept>
		<concept id="21">
			<position x="-20.000000" y="-100.000000"/>
			<objectContingent/>
			<attributeContingent/>
		</concept>
		<concept id="22">
			<position x="0.000000" y="-120.000000"/>
			<objectContingent>
				<labelStyle>
					<offset x="0.000000" y="-4.000000"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>11</objectRef>
			</objectContingent>
			<attributeContingent/>
		</concept>
		<concept id="23">
			<position x="-80.000000" y="-80.000000"/>
			<objectContingent/>
			<attributeContingent>
				<labelStyle>
					<offset x="-3.000000" y="3.000000"/>
					<textAlignment>right</textAlignment>
				</labelStyle>
				<attributeRef>26</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="24">
			<position x="-60.000000" y="-100.000000"/>
			<objectContingent/>
			<attributeContingent/>
		</concept>
		<concept id="25">
			<position x="-40.000000" y="-120.000000"/>
			<objectContingent>
				<labelStyle>
					<offset x="0.000000" y="-4.000000"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>12</objectRef>
			</objectContingent>
			<attributeContingent/>
		</concept>
		<concept id="26">
			<position x="-100.000000" y="-100.000000"/>
			<objectContingent/>
			<attributeContingent>
				<labelStyle>
					<offset x="-3.000000" y="3.000000"/>
					<textAlignment>right</textAlignment>
				</labelStyle>
				<attributeRef>27</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="27">
			<position x="-80.000000" y="-120.000000"/>
			<objectContingent>
				<labelStyle>
					<offset x="0.000000" y="-4.000000"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>13</objectRef>
			</objectContingent>
			<attributeContingent/>
		</concept>
		<concept id="28">
			<position x="-120.000000" y="-120.000000"/>
			<objectContingent>
				<labelStyle>
					<offset x="0.000000" y="-4.000000"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>14</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="-3.000000" y="3.000000"/>
					<textAlignment>right</textAlignment>
				</labelStyle>
				<attributeRef>28</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="29">
			<position x="0.000000" y="-160.000000"/>
			<objectContingent/>
			<attributeContingent/>
		</concept>
		<edge from="1" to="2"/>
		<edge from="1" to="8"/>
		<edge from="2" to="3"/>
		<edge from="2" to="9"/>
		<edge from="3" to="4"/>
		<edge from="3" to="10"/>
		<edge from="4" to="5"/>
		<edge from="4" to="11"/>
		<edge from="5" to="6"/>
		<edge from="5" to="12"/>
		<edge from="6" to="7"/>
		<edge from="6" to="13"/>
		<edge from="7" to="29"/>
		<edge from="8" to="9"/>
		<edge from="8" to="14"/>
		<edge from="9" to="10"/>
		<edge from="9" to="15"/>
		<edge from="10" to="11"/>
		<edge from="10" to="16"/>
		<edge from="11" to="12"/>
		<edge from="11" to="17"/>
		<edge from="12" to="13"/>
		<edge from="12" to="18"/>
		<edge from="13" to="29"/>
		<edge from="14" to="15"/>
		<edge from="14" to="19"/>
		<edge from="15" to="16"/>
		<edge from="15" to="20"/>
		<edge from="16" to="17"/>
		<edge from="16" to="21"/>
		<edge from="17" to="18"/>
		<edge from="17" to="22"/>
		<edge from="18" to="29"/>
		<edge from="19" to="20"/>
		<edge from="19" to="23"/>
		<edge from="20" to="21"/>
		<edge from="20" to="24"/>
		<edge from="21" to="22"/>
		<edge from="21" to="25"/>
		<edge from="22" to="29"/>
		<edge from="23" to="24"/>
		<edge from="23" to="26"/>
		<edge from="24" to="25"/>
		<edge from="24" to="27"/>
		<edge from="25" to="29"/>
		<edge from="26" to="27"/>
		<edge from="26" to="28"/>
		<edge from="27" to="29"/>
		<edge from="28" to="29"/>
	</diagram>
	<diagram title="WinMarks (graphics/disk) of 486/66 PCs">
		<concept id="1">
			<position x="0" y="0"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-20"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>15</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="0" y="20"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<attributeRef>29</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="2">
			<position x="40" y="-40"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-20"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>16</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="20" y="20"/>
					<textAlignment>left</textAlignment>
				</labelStyle>
				<attributeRef>30</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="3">
			<position x="80" y="-80"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-20"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>17</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="20" y="20"/>
					<textAlignment>left</textAlignment>
				</labelStyle>
				<attributeRef>31</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="4">
			<position x="120" y="-120"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-20"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>18</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="20" y="20"/>
					<textAlignment>left</textAlignment>
				</labelStyle>
				<attributeRef>32</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="5">
			<position x="160" y="-160"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-20"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>19</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="20" y="20"/>
					<textAlignment>left</textAlignment>
				</labelStyle>
				<attributeRef>33</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="6">
			<position x="200" y="-200"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-20"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>20</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="20" y="20"/>
					<textAlignment>left</textAlignment>
				</labelStyle>
				<attributeRef>34</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="7">
			<position x="240" y="-240"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-20"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>21</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="20" y="20"/>
					<textAlignment>left</textAlignment>
				</labelStyle>
				<attributeRef>35</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="8">
			<position x="-40" y="-40"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-20"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>22</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="-20" y="20"/>
					<textAlignment>right</textAlignment>
				</labelStyle>
				<attributeRef>36</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="9">
			<position x="-80" y="-80"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-20"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>23</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="-20" y="20"/>
					<textAlignment>right</textAlignment>
				</labelStyle>
				<attributeRef>37</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="10">
			<position x="-120" y="-120"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-20"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>24</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="-20" y="20"/>
					<textAlignment>right</textAlignment>
				</labelStyle>
				<attributeRef>38</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="11">
			<position x="-160" y="-160"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-20"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>25</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="-20" y="20"/>
					<textAlignment>right</textAlignment>
				</labelStyle>
				<attributeRef>39</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="12">
			<position x="-200" y="-200"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-20"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>26</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="-20" y="20"/>
					<textAlignment>right</textAlignment>
				</labelStyle>
				<attributeRef>40</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="13">
			<position x="-240" y="-240"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-20"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>27</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="-20" y="20"/>
					<textAlignment>right</textAlignment>
				</labelStyle>
				<attributeRef>41</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="14">
			<position x="0" y="-80"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-20"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>28</objectRef>
			</objectContingent>
			<attributeContingent/>
		</concept>
		<concept id="15">
			<position x="40" y="-120"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-20"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>29</objectRef>
			</objectContingent>
			<attributeContingent/>
		</concept>
		<concept id="16">
			<position x="80" y="-160"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-20"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>30</objectRef>
			</objectContingent>
			<attributeContingent/>
		</concept>
		<concept id="17">
			<position x="120" y="-200"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-20"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>31</objectRef>
			</objectContingent>
			<attributeContingent/>
		</concept>
		<concept id="18">
			<position x="160" y="-240"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-20"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>32</objectRef>
			</objectContingent>
			<attributeContingent/>
		</concept>
		<concept id="19">
			<position x="200" y="-280"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-20"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>33</objectRef>
			</objectContingent>
			<attributeContingent/>
		</concept>
		<concept id="20">
			<position x="-40" y="-120"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-20"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>34</objectRef>
			</objectContingent>
			<attributeContingent/>
		</concept>
		<concept id="21">
			<position x="-80" y="-160"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-20"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>35</objectRef>
			</objectContingent>
			<attributeContingent/>
		</concept>
		<concept id="22">
			<position x="-120" y="-200"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-20"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>36</objectRef>
			</objectContingent>
			<attributeContingent/>
		</concept>
		<concept id="23">
			<position x="-160" y="-240"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-20"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>37</objectRef>
			</objectContingent>
			<attributeContingent/>
		</concept>
		<concept id="24">
			<position x="-200" y="-280"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-20"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>38</objectRef>
			</objectContingent>
			<attributeContingent/>
		</concept>
		<concept id="25">
			<position x="0" y="-160"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-20"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>39</objectRef>
			</objectContingent>
			<attributeContingent/>
		</concept>
		<concept id="26">
			<position x="40" y="-200"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-20"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>40</objectRef>
			</objectContingent>
			<attributeContingent/>
		</concept>
		<concept id="27">
			<position x="80" y="-240"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-20"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>41</objectRef>
			</objectContingent>
			<attributeContingent/>
		</concept>
		<concept id="28">
			<position x="120" y="-280"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-20"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>42</objectRef>
			</objectContingent>
			<attributeContingent/>
		</concept>
		<concept id="29">
			<position x="160" y="-320"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-20"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>43</objectRef>
			</objectContingent>
			<attributeContingent/>
		</concept>
		<concept id="30">
			<position x="-40" y="-200"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-20"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>44</objectRef>
			</objectContingent>
			<attributeContingent/>
		</concept>
		<concept id="31">
			<position x="-80" y="-240"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-20"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>45</objectRef>
			</objectContingent>
			<attributeContingent/>
		</concept>
		<concept id="32">
			<position x="-120" y="-280"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-20"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>46</objectRef>
			</objectContingent>
			<attributeContingent/>
		</concept>
		<concept id="33">
			<position x="-160" y="-320"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-20"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>47</objectRef>
			</objectContingent>
			<attributeContingent/>
		</concept>
		<concept id="34">
			<position x="0" y="-240"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-20"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>48</objectRef>
			</objectContingent>
			<attributeContingent/>
		</concept>
		<concept id="35">
			<position x="40" y="-280"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-20"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>49</objectRef>
			</objectContingent>
			<attributeContingent/>
		</concept>
		<concept id="36">
			<position x="80" y="-320"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-20"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>50</objectRef>
			</objectContingent>
			<attributeContingent/>
		</concept>
		<concept id="37">
			<position x="120" y="-360"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-20"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>51</objectRef>
			</objectContingent>
			<attributeContingent/>
		</concept>
		<concept id="38">
			<position x="-40" y="-280"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-20"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>52</objectRef>
			</objectContingent>
			<attributeContingent/>
		</concept>
		<concept id="39">
			<position x="-80" y="-320"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-20"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>53</objectRef>
			</objectContingent>
			<attributeContingent/>
		</concept>
		<concept id="40">
			<position x="-120" y="-360"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-20"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>54</objectRef>
			</objectContingent>
			<attributeContingent/>
		</concept>
		<concept id="41">
			<position x="0" y="-320"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-20"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>55</objectRef>
			</objectContingent>
			<attributeContingent/>
		</concept>
		<concept id="42">
			<position x="40" y="-360"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-20"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>56</objectRef>
			</objectContingent>
			<attributeContingent/>
		</concept>
		<concept id="43">
			<position x="80" y="-400"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-20"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>57</objectRef>
			</objectContingent>
			<attributeContingent/>
		</concept>
		<concept id="44">
			<position x="-40" y="-360"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-20"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>58</objectRef>
			</objectContingent>
			<attributeContingent/>
		</concept>
		<concept id="45">
			<position x="-80" y="-400"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-20"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>59</objectRef>
			</objectContingent>
			<attributeContingent/>
		</concept>
		<concept id="46">
			<position x="0" y="-400"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-20"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>60</objectRef>
			</objectContingent>
			<attributeContingent/>
		</concept>
		<concept id="47">
			<position x="40" y="-440"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-20"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>61</objectRef>
			</objectContingent>
			<attributeContingent/>
		</concept>
		<concept id="48">
			<position x="-40" y="-440"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-20"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>62</objectRef>
			</objectContingent>
			<attributeContingent/>
		</concept>
		<concept id="49">
			<position x="0" y="-480"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-20"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>63</objectRef>
			</objectContingent>
			<attributeContingent/>
		</concept>
		<edge from="1" to="2"/>
		<edge from="1" to="8"/>
		<edge from="2" to="3"/>
		<edge from="2" to="14"/>
		<edge from="3" to="4"/>
		<edge from="3" to="15"/>
		<edge from="4" to="5"/>
		<edge from="4" to="16"/>
		<edge from="5" to="6"/>
		<edge from="5" to="17"/>
		<edge from="6" to="7"/>
		<edge from="6" to="18"/>
		<edge from="7" to="19"/>
		<edge from="8" to="9"/>
		<edge from="8" to="14"/>
		<edge from="9" to="10"/>
		<edge from="9" to="20"/>
		<edge from="10" to="11"/>
		<edge from="10" to="21"/>
		<edge from="11" to="12"/>
		<edge from="11" to="22"/>
		<edge from="12" to="13"/>
		<edge from="12" to="23"/>
		<edge from="13" to="24"/>
		<edge from="14" to="15"/>
		<edge from="14" to="20"/>
		<edge from="15" to="16"/>
		<edge from="15" to="25"/>
		<edge from="16" to="17"/>
		<edge from="16" to="26"/>
		<edge from="17" to="18"/>
		<edge from="17" to="27"/>
		<edge from="18" to="19"/>
		<edge from="18" to="28"/>
		<edge from="19" to="29"/>
		<edge from="20" to="21"/>
		<edge from="20" to="25"/>
		<edge from="21" to="22"/>
		<edge from="21" to="30"/>
		<edge from="22" to="23"/>
		<edge from="22" to="31"/>
		<edge from="23" to="24"/>
		<edge from="23" to="32"/>
		<edge from="24" to="33"/>
		<edge from="25" to="26"/>
		<edge from="25" to="30"/>
		<edge from="26" to="27"/>
		<edge from="26" to="34"/>
		<edge from="27" to="28"/>
		<edge from="27" to="35"/>
		<edge from="28" to="29"/>
		<edge from="28" to="36"/>
		<edge from="29" to="37"/>
		<edge from="30" to="31"/>
		<edge from="30" to="34"/>
		<edge from="31" to="32"/>
		<edge from="31" to="38"/>
		<edge from="32" to="33"/>
		<edge from="32" to="39"/>
		<edge from="33" to="40"/>
		<edge from="34" to="35"/>
		<edge from="34" to="38"/>
		<edge from="35" to="36"/>
		<edge from="35" to="41"/>
		<edge from="36" to="37"/>
		<edge from="36" to="42"/>
		<edge from="37" to="43"/>
		<edge from="38" to="39"/>
		<edge from="38" to="41"/>
		<edge from="39" to="40"/>
		<edge from="39" to="44"/>
		<edge from="40" to="45"/>
		<edge from="41" to="42"/>
		<edge from="41" to="44"/>
		<edge from="42" to="43"/>
		<edge from="42" to="46"/>
		<edge from="43" to="47"/>
		<edge from="44" to="45"/>
		<edge from="44" to="46"/>
		<edge from="45" to="48"/>
		<edge from="46" to="47"/>
		<edge from="46" to="48"/>
		<edge from="47" to="49"/>
		<edge from="48" to="49"/>
	</diagram>
	<diagram title="Types of bus systems of the 486/66 PCs">
		<concept id="1">
			<position x="0" y="0"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-20"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>64</objectRef>
			</objectContingent>
			<attributeContingent/>
		</concept>
		<concept id="2">
			<position x="80" y="-80"/>
			<objectContingent>
				<labelStyle>
					<offset x="12" y="-12"/>
					<textAlignment>left</textAlignment>
				</labelStyle>
				<objectRef>65</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="12" y="12"/>
					<textAlignment>left</textAlignment>
				</labelStyle>
				<attributeRef>42</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="3">
			<position x="80" y="-160"/>
			<objectContingent>
				<labelStyle>
					<offset x="12" y="-12"/>
					<textAlignment>left</textAlignment>
				</labelStyle>
				<objectRef>66</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="12" y="12"/>
					<textAlignment>left</textAlignment>
				</labelStyle>
				<attributeRef>43</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="4">
			<position x="-80" y="-120"/>
			<objectContingent>
				<labelStyle>
					<offset x="-12" y="-12"/>
					<textAlignment>right</textAlignment>
				</labelStyle>
				<objectRef>67</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="-12" y="12"/>
					<textAlignment>right</textAlignment>
				</labelStyle>
				<attributeRef>44</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="5">
			<position x="0" y="-240"/>
			<objectContingent/>
			<attributeContingent/>
		</concept>
		<edge from="1" to="2"/>
		<edge from="1" to="4"/>
		<edge from="2" to="3"/>
		<edge from="3" to="5"/>
		<edge from="4" to="5"/>
	</diagram>
	<diagram title="Sizes of harddisks (ordinal)">
		<concept id="1">
			<position x="0" y="0"/>
			<objectContingent>
				<labelStyle>
					<offset x="15" y="-15"/>
					<textAlignment>left</textAlignment>
				</labelStyle>
				<objectRef>68</objectRef>
			</objectContingent>
			<attributeContingent/>
		</concept>
		<concept id="2">
			<position x="0" y="-100"/>
			<objectContingent>
				<labelStyle>
					<offset x="15" y="-15"/>
					<textAlignment>left</textAlignment>
				</labelStyle>
				<objectRef>69</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="15" y="15"/>
					<textAlignment>left</textAlignment>
				</labelStyle>
				<attributeRef>45</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="3">
			<position x="0" y="-200"/>
			<objectContingent>
				<labelStyle>
					<offset x="15" y="-15"/>
					<textAlignment>left</textAlignment>
				</labelStyle>
				<objectRef>70</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="15" y="15"/>
					<textAlignment>left</textAlignment>
				</labelStyle>
				<attributeRef>46</attributeRef>
			</attributeContingent>
		</concept>
		<edge from="1" to="2"/>
		<edge from="2" to="3"/>
	</diagram>
	<diagram title="Video throughput (1000 operations per second)">
		<concept id="1">
			<position x="0.000000" y="0.000000"/>
			<objectContingent/>
			<attributeContingent>
				<labelStyle>
					<offset x="-3.000000" y="3.000000"/>
					<textAlignment>right</textAlignment>
				</labelStyle>
				<attributeRef>47</attributeRef>
				<attributeRef>48</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="2">
			<position x="20.000000" y="-20.000000"/>
			<objectContingent/>
			<attributeContingent>
				<labelStyle>
					<offset x="3.000000" y="3.000000"/>
					<textAlignment>left</textAlignment>
				</labelStyle>
				<attributeRef>49</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="3">
			<position x="40.000000" y="-40.000000"/>
			<objectContingent/>
			<attributeContingent>
				<labelStyle>
					<offset x="3.000000" y="3.000000"/>
					<textAlignment>left</textAlignment>
				</labelStyle>
				<attributeRef>50</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="4">
			<position x="60.000000" y="-60.000000"/>
			<objectContingent/>
			<attributeContingent>
				<labelStyle>
					<offset x="3.000000" y="3.000000"/>
					<textAlignment>left</textAlignment>
				</labelStyle>
				<attributeRef>51</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="5">
			<position x="80.000000" y="-80.000000"/>
			<objectContingent/>
			<attributeContingent>
				<labelStyle>
					<offset x="3.000000" y="3.000000"/>
					<textAlignment>left</textAlignment>
				</labelStyle>
				<attributeRef>52</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="6">
			<position x="100.000000" y="-100.000000"/>
			<objectContingent/>
			<attributeContingent>
				<labelStyle>
					<offset x="3.000000" y="3.000000"/>
					<textAlignment>left</textAlignment>
				</labelStyle>
				<attributeRef>53</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="7">
			<position x="120.000000" y="-120.000000"/>
			<objectContingent>
				<labelStyle>
					<offset x="0.000000" y="-4.000000"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>71</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="3.000000" y="3.000000"/>
					<textAlignment>left</textAlignment>
				</labelStyle>
				<attributeRef>54</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="8">
			<position x="-20.000000" y="-20.000000"/>
			<objectContingent/>
			<attributeContingent>
				<labelStyle>
					<offset x="-3.000000" y="3.000000"/>
					<textAlignment>right</textAlignment>
				</labelStyle>
				<attributeRef>55</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="9">
			<position x="0.000000" y="-40.000000"/>
			<objectContingent/>
			<attributeContingent/>
		</concept>
		<concept id="10">
			<position x="20.000000" y="-60.000000"/>
			<objectContingent/>
			<attributeContingent/>
		</concept>
		<concept id="11">
			<position x="40.000000" y="-80.000000"/>
			<objectContingent/>
			<attributeContingent/>
		</concept>
		<concept id="12">
			<position x="60.000000" y="-100.000000"/>
			<objectContingent/>
			<attributeContingent/>
		</concept>
		<concept id="13">
			<position x="80.000000" y="-120.000000"/>
			<objectContingent>
				<labelStyle>
					<offset x="0.000000" y="-4.000000"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>72</objectRef>
			</objectContingent>
			<attributeContingent/>
		</concept>
		<concept id="14">
			<position x="-40.000000" y="-40.000000"/>
			<objectContingent/>
			<attributeContingent>
				<labelStyle>
					<offset x="-3.000000" y="3.000000"/>
					<textAlignment>right</textAlignment>
				</labelStyle>
				<attributeRef>56</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="15">
			<position x="-20.000000" y="-60.000000"/>
			<objectContingent/>
			<attributeContingent/>
		</concept>
		<concept id="16">
			<position x="0.000000" y="-80.000000"/>
			<objectContingent/>
			<attributeContingent/>
		</concept>
		<concept id="17">
			<position x="20.000000" y="-100.000000"/>
			<objectContingent/>
			<attributeContingent/>
		</concept>
		<concept id="18">
			<position x="40.000000" y="-120.000000"/>
			<objectContingent>
				<labelStyle>
					<offset x="0.000000" y="-4.000000"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>73</objectRef>
			</objectContingent>
			<attributeContingent/>
		</concept>
		<concept id="19">
			<position x="-60.000000" y="-60.000000"/>
			<objectContingent/>
			<attributeContingent>
				<labelStyle>
					<offset x="-3.000000" y="3.000000"/>
					<textAlignment>right</textAlignment>
				</labelStyle>
				<attributeRef>57</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="20">
			<position x="-40.000000" y="-80.000000"/>
			<objectContingent/>
			<attributeContingent/>
		</concept>
		<concept id="21">
			<position x="-20.000000" y="-100.000000"/>
			<objectContingent/>
			<attributeContingent/>
		</concept>
		<concept id="22">
			<position x="0.000000" y="-120.000000"/>
			<objectContingent>
				<labelStyle>
					<offset x="0.000000" y="-4.000000"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>74</objectRef>
			</objectContingent>
			<attributeContingent/>
		</concept>
		<concept id="23">
			<position x="-80.000000" y="-80.000000"/>
			<objectContingent/>
			<attributeContingent>
				<labelStyle>
					<offset x="-3.000000" y="3.000000"/>
					<textAlignment>right</textAlignment>
				</labelStyle>
				<attributeRef>58</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="24">
			<position x="-60.000000" y="-100.000000"/>
			<objectContingent/>
			<attributeContingent/>
		</concept>
		<concept id="25">
			<position x="-40.000000" y="-120.000000"/>
			<objectContingent>
				<labelStyle>
					<offset x="0.000000" y="-4.000000"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>75</objectRef>
			</objectContingent>
			<attributeContingent/>
		</concept>
		<concept id="26">
			<position x="-100.000000" y="-100.000000"/>
			<objectContingent/>
			<attributeContingent>
				<labelStyle>
					<offset x="-3.000000" y="3.000000"/>
					<textAlignment>right</textAlignment>
				</labelStyle>
				<attributeRef>59</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="27">
			<position x="-80.000000" y="-120.000000"/>
			<objectContingent>
				<labelStyle>
					<offset x="0.000000" y="-4.000000"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>76</objectRef>
			</objectContingent>
			<attributeContingent/>
		</concept>
		<concept id="28">
			<position x="-120.000000" y="-120.000000"/>
			<objectContingent>
				<labelStyle>
					<offset x="0.000000" y="-4.000000"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>77</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="-3.000000" y="3.000000"/>
					<textAlignment>right</textAlignment>
				</labelStyle>
				<attributeRef>60</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="29">
			<position x="0.000000" y="-160.000000"/>
			<objectContingent/>
			<attributeContingent/>
		</concept>
		<edge from="1" to="2"/>
		<edge from="1" to="8"/>
		<edge from="2" to="3"/>
		<edge from="2" to="9"/>
		<edge from="3" to="4"/>
		<edge from="3" to="10"/>
		<edge from="4" to="5"/>
		<edge from="4" to="11"/>
		<edge from="5" to="6"/>
		<edge from="5" to="12"/>
		<edge from="6" to="7"/>
		<edge from="6" to="13"/>
		<edge from="7" to="29"/>
		<edge from="8" to="9"/>
		<edge from="8" to="14"/>
		<edge from="9" to="10"/>
		<edge from="9" to="15"/>
		<edge from="10" to="11"/>
		<edge from="10" to="16"/>
		<edge from="11" to="12"/>
		<edge from="11" to="17"/>
		<edge from="12" to="13"/>
		<edge from="12" to="18"/>
		<edge from="13" to="29"/>
		<edge from="14" to="15"/>
		<edge from="14" to="19"/>
		<edge from="15" to="16"/>
		<edge from="15" to="20"/>
		<edge from="16" to="17"/>
		<edge from="16" to="21"/>
		<edge from="17" to="18"/>
		<edge from="17" to="22"/>
		<edge from="18" to="29"/>
		<edge from="19" to="20"/>
		<edge from="19" to="23"/>
		<edge from="20" to="21"/>
		<edge from="20" to="24"/>
		<edge from="21" to="22"/>
		<edge from="21" to="25"/>
		<edge from="22" to="29"/>
		<edge from="23" to="24"/>
		<edge from="23" to="26"/>
		<edge from="24" to="25"/>
		<edge from="24" to="27"/>
		<edge from="25" to="29"/>
		<edge from="26" to="27"/>
		<edge from="26" to="28"/>
		<edge from="27" to="29"/>
		<edge from="28" to="29"/>
	</diagram>
	<diagram title="DOS-Win-Mark">
		<concept id="1">
			<position x="0" y="0"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-20"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>78</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="0" y="20"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<attributeRef>61</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="2">
			<position x="40" y="-40"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-20"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>79</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="20" y="20"/>
					<textAlignment>left</textAlignment>
				</labelStyle>
				<attributeRef>62</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="3">
			<position x="80" y="-80"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-20"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>80</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="20" y="20"/>
					<textAlignment>left</textAlignment>
				</labelStyle>
				<attributeRef>63</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="4">
			<position x="120" y="-120"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-20"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>81</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="20" y="20"/>
					<textAlignment>left</textAlignment>
				</labelStyle>
				<attributeRef>64</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="5">
			<position x="160" y="-160"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-20"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>82</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="20" y="20"/>
					<textAlignment>left</textAlignment>
				</labelStyle>
				<attributeRef>65</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="6">
			<position x="200" y="-200"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-20"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>83</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="20" y="20"/>
					<textAlignment>left</textAlignment>
				</labelStyle>
				<attributeRef>66</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="7">
			<position x="240" y="-240"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-20"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>84</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="20" y="20"/>
					<textAlignment>left</textAlignment>
				</labelStyle>
				<attributeRef>67</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="8">
			<position x="-40" y="-40"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-20"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>85</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="-20" y="20"/>
					<textAlignment>right</textAlignment>
				</labelStyle>
				<attributeRef>68</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="9">
			<position x="-80" y="-80"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-20"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>86</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="-20" y="20"/>
					<textAlignment>right</textAlignment>
				</labelStyle>
				<attributeRef>69</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="10">
			<position x="-120" y="-120"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-20"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>87</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="-20" y="20"/>
					<textAlignment>right</textAlignment>
				</labelStyle>
				<attributeRef>70</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="11">
			<position x="-160" y="-160"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-20"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>88</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="-20" y="20"/>
					<textAlignment>right</textAlignment>
				</labelStyle>
				<attributeRef>71</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="12">
			<position x="-200" y="-200"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-20"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>89</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="-20" y="20"/>
					<textAlignment>right</textAlignment>
				</labelStyle>
				<attributeRef>72</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="13">
			<position x="-240" y="-240"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-20"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>90</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="-20" y="20"/>
					<textAlignment>right</textAlignment>
				</labelStyle>
				<attributeRef>73</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="14">
			<position x="0" y="-80"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-20"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>91</objectRef>
			</objectContingent>
			<attributeContingent/>
		</concept>
		<concept id="15">
			<position x="40" y="-120"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-20"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>92</objectRef>
			</objectContingent>
			<attributeContingent/>
		</concept>
		<concept id="16">
			<position x="80" y="-160"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-20"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>93</objectRef>
			</objectContingent>
			<attributeContingent/>
		</concept>
		<concept id="17">
			<position x="120" y="-200"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-20"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>94</objectRef>
			</objectContingent>
			<attributeContingent/>
		</concept>
		<concept id="18">
			<position x="160" y="-240"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-20"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>95</objectRef>
			</objectContingent>
			<attributeContingent/>
		</concept>
		<concept id="19">
			<position x="200" y="-280"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-20"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>96</objectRef>
			</objectContingent>
			<attributeContingent/>
		</concept>
		<concept id="20">
			<position x="-40" y="-120"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-20"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>97</objectRef>
			</objectContingent>
			<attributeContingent/>
		</concept>
		<concept id="21">
			<position x="-80" y="-160"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-20"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>98</objectRef>
			</objectContingent>
			<attributeContingent/>
		</concept>
		<concept id="22">
			<position x="-120" y="-200"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-20"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>99</objectRef>
			</objectContingent>
			<attributeContingent/>
		</concept>
		<concept id="23">
			<position x="-160" y="-240"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-20"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>100</objectRef>
			</objectContingent>
			<attributeContingent/>
		</concept>
		<concept id="24">
			<position x="-200" y="-280"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-20"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>101</objectRef>
			</objectContingent>
			<attributeContingent/>
		</concept>
		<concept id="25">
			<position x="0" y="-160"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-20"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>102</objectRef>
			</objectContingent>
			<attributeContingent/>
		</concept>
		<concept id="26">
			<position x="40" y="-200"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-20"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>103</objectRef>
			</objectContingent>
			<attributeContingent/>
		</concept>
		<concept id="27">
			<position x="80" y="-240"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-20"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>104</objectRef>
			</objectContingent>
			<attributeContingent/>
		</concept>
		<concept id="28">
			<position x="120" y="-280"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-20"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>105</objectRef>
			</objectContingent>
			<attributeContingent/>
		</concept>
		<concept id="29">
			<position x="160" y="-320"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-20"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>106</objectRef>
			</objectContingent>
			<attributeContingent/>
		</concept>
		<concept id="30">
			<position x="-40" y="-200"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-20"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>107</objectRef>
			</objectContingent>
			<attributeContingent/>
		</concept>
		<concept id="31">
			<position x="-80" y="-240"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-20"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>108</objectRef>
			</objectContingent>
			<attributeContingent/>
		</concept>
		<concept id="32">
			<position x="-120" y="-280"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-20"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>109</objectRef>
			</objectContingent>
			<attributeContingent/>
		</concept>
		<concept id="33">
			<position x="-160" y="-320"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-20"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>110</objectRef>
			</objectContingent>
			<attributeContingent/>
		</concept>
		<concept id="34">
			<position x="0" y="-240"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-20"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>111</objectRef>
			</objectContingent>
			<attributeContingent/>
		</concept>
		<concept id="35">
			<position x="40" y="-280"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-20"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>112</objectRef>
			</objectContingent>
			<attributeContingent/>
		</concept>
		<concept id="36">
			<position x="80" y="-320"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-20"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>113</objectRef>
			</objectContingent>
			<attributeContingent/>
		</concept>
		<concept id="37">
			<position x="120" y="-360"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-20"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>114</objectRef>
			</objectContingent>
			<attributeContingent/>
		</concept>
		<concept id="38">
			<position x="-40" y="-280"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-20"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>115</objectRef>
			</objectContingent>
			<attributeContingent/>
		</concept>
		<concept id="39">
			<position x="-80" y="-320"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-20"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>116</objectRef>
			</objectContingent>
			<attributeContingent/>
		</concept>
		<concept id="40">
			<position x="-120" y="-360"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-20"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>117</objectRef>
			</objectContingent>
			<attributeContingent/>
		</concept>
		<concept id="41">
			<position x="0" y="-320"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-20"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>118</objectRef>
			</objectContingent>
			<attributeContingent/>
		</concept>
		<concept id="42">
			<position x="40" y="-360"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-20"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>119</objectRef>
			</objectContingent>
			<attributeContingent/>
		</concept>
		<concept id="43">
			<position x="80" y="-400"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-20"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>120</objectRef>
			</objectContingent>
			<attributeContingent/>
		</concept>
		<concept id="44">
			<position x="-40" y="-360"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-20"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>121</objectRef>
			</objectContingent>
			<attributeContingent/>
		</concept>
		<concept id="45">
			<position x="-80" y="-400"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-20"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>122</objectRef>
			</objectContingent>
			<attributeContingent/>
		</concept>
		<concept id="46">
			<position x="0" y="-400"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-20"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>123</objectRef>
			</objectContingent>
			<attributeContingent/>
		</concept>
		<concept id="47">
			<position x="40" y="-440"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-20"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>124</objectRef>
			</objectContingent>
			<attributeContingent/>
		</concept>
		<concept id="48">
			<position x="-40" y="-440"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-20"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>125</objectRef>
			</objectContingent>
			<attributeContingent/>
		</concept>
		<concept id="49">
			<position x="0" y="-480"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-20"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>126</objectRef>
			</objectContingent>
			<attributeContingent/>
		</concept>
		<edge from="1" to="2"/>
		<edge from="1" to="8"/>
		<edge from="2" to="3"/>
		<edge from="2" to="14"/>
		<edge from="3" to="4"/>
		<edge from="3" to="15"/>
		<edge from="4" to="5"/>
		<edge from="4" to="16"/>
		<edge from="5" to="6"/>
		<edge from="5" to="17"/>
		<edge from="6" to="7"/>
		<edge from="6" to="18"/>
		<edge from="7" to="19"/>
		<edge from="8" to="9"/>
		<edge from="8" to="14"/>
		<edge from="9" to="10"/>
		<edge from="9" to="20"/>
		<edge from="10" to="11"/>
		<edge from="10" to="21"/>
		<edge from="11" to="12"/>
		<edge from="11" to="22"/>
		<edge from="12" to="13"/>
		<edge from="12" to="23"/>
		<edge from="13" to="24"/>
		<edge from="14" to="15"/>
		<edge from="14" to="20"/>
		<edge from="15" to="16"/>
		<edge from="15" to="25"/>
		<edge from="16" to="17"/>
		<edge from="16" to="26"/>
		<edge from="17" to="18"/>
		<edge from="17" to="27"/>
		<edge from="18" to="19"/>
		<edge from="18" to="28"/>
		<edge from="19" to="29"/>
		<edge from="20" to="21"/>
		<edge from="20" to="25"/>
		<edge from="21" to="22"/>
		<edge from="21" to="30"/>
		<edge from="22" to="23"/>
		<edge from="22" to="31"/>
		<edge from="23" to="24"/>
		<edge from="23" to="32"/>
		<edge from="24" to="33"/>
		<edge from="25" to="26"/>
		<edge from="25" to="30"/>
		<edge from="26" to="27"/>
		<edge from="26" to="34"/>
		<edge from="27" to="28"/>
		<edge from="27" to="35"/>
		<edge from="28" to="29"/>
		<edge from="28" to="36"/>
		<edge from="29" to="37"/>
		<edge from="30" to="31"/>
		<edge from="30" to="34"/>
		<edge from="31" to="32"/>
		<edge from="31" to="38"/>
		<edge from="32" to="33"/>
		<edge from="32" to="39"/>
		<edge from="33" to="40"/>
		<edge from="34" to="35"/>
		<edge from="34" to="38"/>
		<edge from="35" to="36"/>
		<edge from="35" to="41"/>
		<edge from="36" to="37"/>
		<edge from="36" to="42"/>
		<edge from="37" to="43"/>
		<edge from="38" to="39"/>
		<edge from="38" to="41"/>
		<edge from="39" to="40"/>
		<edge from="39" to="44"/>
		<edge from="40" to="45"/>
		<edge from="41" to="42"/>
		<edge from="41" to="44"/>
		<edge from="42" to="43"/>
		<edge from="42" to="46"/>
		<edge from="43" to="47"/>
		<edge from="44" to="45"/>
		<edge from="44" to="46"/>
		<edge from="45" to="48"/>
		<edge from="46" to="47"/>
		<edge from="46" to="48"/>
		<edge from="47" to="49"/>
		<edge from="48" to="49"/>
	</diagram>
	<diagram title="Means of distribution">
		<concept id="1">
			<position x="0" y="0"/>
			<objectContingent/>
			<attributeContingent/>
		</concept>
		<concept id="2">
			<position x="-70" y="-70"/>
			<objectContingent/>
			<attributeContingent>
				<labelStyle>
					<offset x="0" y="17.5"/>
					<textAlignment>right</textAlignment>
				</labelStyle>
				<attributeRef>74</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="3">
			<position x="-140" y="-140"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-21"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>127</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="0" y="17.5"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<attributeRef>75</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="4">
			<position x="70" y="-70"/>
			<objectContingent/>
			<attributeContingent>
				<labelStyle>
					<offset x="0" y="17.5"/>
					<textAlignment>left</textAlignment>
				</labelStyle>
				<attributeRef>76</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="5">
			<position x="140" y="-140"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-21"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>128</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="0" y="17.5"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<attributeRef>77</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="6">
			<position x="0" y="-140"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-21"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>129</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="0" y="17.5"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<attributeRef>78</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="7">
			<position x="0" y="-210"/>
			<objectContingent/>
			<attributeContingent/>
		</concept>
		<edge from="1" to="2"/>
		<edge from="1" to="4"/>
		<edge from="2" to="3"/>
		<edge from="2" to="6"/>
		<edge from="3" to="7"/>
		<edge from="4" to="5"/>
		<edge from="4" to="6"/>
		<edge from="5" to="7"/>
		<edge from="6" to="7"/>
	</diagram>
	<diagram title="Cases">
		<concept id="1">
			<position x="0" y="0"/>
			<objectContingent/>
			<attributeContingent/>
		</concept>
		<concept id="2">
			<position x="50" y="-50"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-7.5"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>130</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="0" y="8.75"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<attributeRef>79</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="3">
			<position x="100" y="-100"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-7.5"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>131</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="0" y="8.75"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<attributeRef>80</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="4">
			<position x="0" y="-100"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-7.5"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>132</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="0" y="8.75"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<attributeRef>81</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="5">
			<position x="-50" y="-50"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-7.5"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>133</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="0" y="8.75"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<attributeRef>82</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="6">
			<position x="-100" y="-100"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-7.5"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>134</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="0" y="8.75"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<attributeRef>83</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="7">
			<position x="0" y="-175"/>
			<objectContingent/>
			<attributeContingent/>
		</concept>
		<edge from="1" to="2"/>
		<edge from="1" to="5"/>
		<edge from="2" to="3"/>
		<edge from="2" to="4"/>
		<edge from="3" to="7"/>
		<edge from="4" to="7"/>
		<edge from="5" to="6"/>
		<edge from="6" to="7"/>
	</diagram>
	<diagram title="Accessible drives">
		<concept id="1">
			<position x="0" y="0"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-15"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>135</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="-17.5" y="12"/>
					<textAlignment>right</textAlignment>
				</labelStyle>
				<attributeRef>84</attributeRef>
				<attributeRef>85</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="2">
			<position x="50" y="-50"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-15"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>136</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="15" y="15"/>
					<textAlignment>left</textAlignment>
				</labelStyle>
				<attributeRef>86</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="3">
			<position x="100" y="-100"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-15"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>137</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="15" y="15"/>
					<textAlignment>left</textAlignment>
				</labelStyle>
				<attributeRef>87</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="4">
			<position x="150" y="-150"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-15"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>138</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="15" y="15"/>
					<textAlignment>left</textAlignment>
				</labelStyle>
				<attributeRef>88</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="5">
			<position x="-50" y="-50"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-15"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>139</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="-4.5" y="14"/>
					<textAlignment>right</textAlignment>
				</labelStyle>
				<attributeRef>89</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="6">
			<position x="0" y="-100"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-15"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>140</objectRef>
			</objectContingent>
			<attributeContingent/>
		</concept>
		<concept id="7">
			<position x="50" y="-150"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-15"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>141</objectRef>
			</objectContingent>
			<attributeContingent/>
		</concept>
		<concept id="8">
			<position x="100" y="-200"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-15"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>142</objectRef>
			</objectContingent>
			<attributeContingent/>
		</concept>
		<concept id="9">
			<position x="-100" y="-100"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-15"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>143</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="-5.5" y="15"/>
					<textAlignment>right</textAlignment>
				</labelStyle>
				<attributeRef>90</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="10">
			<position x="-50" y="-150"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-15"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>144</objectRef>
			</objectContingent>
			<attributeContingent/>
		</concept>
		<concept id="11">
			<position x="0" y="-200"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-15"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>145</objectRef>
			</objectContingent>
			<attributeContingent/>
		</concept>
		<concept id="12">
			<position x="50" y="-250"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-15"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>146</objectRef>
			</objectContingent>
			<attributeContingent/>
		</concept>
		<concept id="13">
			<position x="-150" y="-150"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-15"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>147</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="-11" y="14"/>
					<textAlignment>right</textAlignment>
				</labelStyle>
				<attributeRef>91</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="14">
			<position x="-100" y="-200"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-15"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>148</objectRef>
			</objectContingent>
			<attributeContingent/>
		</concept>
		<concept id="15">
			<position x="-50" y="-250"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-15"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>149</objectRef>
			</objectContingent>
			<attributeContingent/>
		</concept>
		<concept id="16">
			<position x="0" y="-300"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-15"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>150</objectRef>
			</objectContingent>
			<attributeContingent/>
		</concept>
		<concept id="17">
			<position x="-200" y="-200"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-15"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>151</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="-8" y="16"/>
					<textAlignment>right</textAlignment>
				</labelStyle>
				<attributeRef>92</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="18">
			<position x="-150" y="-250"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-15"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>152</objectRef>
			</objectContingent>
			<attributeContingent/>
		</concept>
		<concept id="19">
			<position x="-100" y="-300"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-15"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>153</objectRef>
			</objectContingent>
			<attributeContingent/>
		</concept>
		<concept id="20">
			<position x="-50" y="-350"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-15"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>154</objectRef>
			</objectContingent>
			<attributeContingent/>
		</concept>
		<concept id="21">
			<position x="-250" y="-250"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-15"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>155</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="-7.5" y="12"/>
					<textAlignment>right</textAlignment>
				</labelStyle>
				<attributeRef>93</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="22">
			<position x="-200" y="-300"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-15"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>156</objectRef>
			</objectContingent>
			<attributeContingent/>
		</concept>
		<concept id="23">
			<position x="-150" y="-350"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-15"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>157</objectRef>
			</objectContingent>
			<attributeContingent/>
		</concept>
		<concept id="24">
			<position x="-100" y="-400"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-15"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>158</objectRef>
			</objectContingent>
			<attributeContingent/>
		</concept>
		<concept id="25">
			<position x="-300" y="-300"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-15"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>159</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="-7" y="15"/>
					<textAlignment>right</textAlignment>
				</labelStyle>
				<attributeRef>94</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="26">
			<position x="-250" y="-350"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-15"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>160</objectRef>
			</objectContingent>
			<attributeContingent/>
		</concept>
		<concept id="27">
			<position x="-200" y="-400"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-15"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>161</objectRef>
			</objectContingent>
			<attributeContingent/>
		</concept>
		<concept id="28">
			<position x="-150" y="-450"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-15"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>162</objectRef>
			</objectContingent>
			<attributeContingent/>
		</concept>
		<concept id="29">
			<position x="-350" y="-350"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-15"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>163</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="-10" y="14"/>
					<textAlignment>right</textAlignment>
				</labelStyle>
				<attributeRef>95</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="30">
			<position x="-300" y="-400"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-15"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>164</objectRef>
			</objectContingent>
			<attributeContingent/>
		</concept>
		<concept id="31">
			<position x="-250" y="-450"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-15"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>165</objectRef>
			</objectContingent>
			<attributeContingent/>
		</concept>
		<concept id="32">
			<position x="-200" y="-500"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-15"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>166</objectRef>
			</objectContingent>
			<attributeContingent/>
		</concept>
		<edge from="1" to="2"/>
		<edge from="1" to="5"/>
		<edge from="2" to="3"/>
		<edge from="2" to="6"/>
		<edge from="3" to="4"/>
		<edge from="3" to="7"/>
		<edge from="4" to="8"/>
		<edge from="5" to="6"/>
		<edge from="5" to="9"/>
		<edge from="6" to="7"/>
		<edge from="6" to="10"/>
		<edge from="7" to="8"/>
		<edge from="7" to="11"/>
		<edge from="8" to="12"/>
		<edge from="9" to="10"/>
		<edge from="9" to="13"/>
		<edge from="10" to="11"/>
		<edge from="10" to="14"/>
		<edge from="11" to="12"/>
		<edge from="11" to="15"/>
		<edge from="12" to="16"/>
		<edge from="13" to="14"/>
		<edge from="13" to="17"/>
		<edge from="14" to="15"/>
		<edge from="14" to="18"/>
		<edge from="15" to="16"/>
		<edge from="15" to="19"/>
		<edge from="16" to="20"/>
		<edge from="17" to="18"/>
		<edge from="17" to="21"/>
		<edge from="18" to="19"/>
		<edge from="18" to="22"/>
		<edge from="19" to="20"/>
		<edge from="19" to="23"/>
		<edge from="20" to="24"/>
		<edge from="21" to="22"/>
		<edge from="21" to="25"/>
		<edge from="22" to="23"/>
		<edge from="22" to="26"/>
		<edge from="23" to="24"/>
		<edge from="23" to="27"/>
		<edge from="24" to="28"/>
		<edge from="25" to="26"/>
		<edge from="25" to="29"/>
		<edge from="26" to="27"/>
		<edge from="26" to="30"/>
		<edge from="27" to="28"/>
		<edge from="27" to="31"/>
		<edge from="28" to="32"/>
		<edge from="29" to="30"/>
		<edge from="30" to="31"/>
		<edge from="31" to="32"/>
	</diagram>
	<diagram title="Power supply and connectors">
		<concept id="1">
			<position x="0" y="0"/>
			<objectContingent/>
			<attributeContingent/>
		</concept>
		<concept id="2">
			<position x="0" y="-60"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-9"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>167</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="9" y="9"/>
					<textAlignment>left</textAlignment>
				</labelStyle>
				<attributeRef>96</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="3">
			<position x="0" y="-120"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-9"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>168</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="9" y="9"/>
					<textAlignment>left</textAlignment>
				</labelStyle>
				<attributeRef>97</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="4">
			<position x="0" y="-180"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-9"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>169</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="9" y="9"/>
					<textAlignment>left</textAlignment>
				</labelStyle>
				<attributeRef>98</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="5">
			<position x="0" y="-240"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-9"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>170</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="9" y="9"/>
					<textAlignment>left</textAlignment>
				</labelStyle>
				<attributeRef>99</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="6">
			<position x="0" y="-300"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-9"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>171</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="9" y="9"/>
					<textAlignment>left</textAlignment>
				</labelStyle>
				<attributeRef>100</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="7">
			<position x="-180" y="-60"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-9"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>172</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="-9" y="9"/>
					<textAlignment>right</textAlignment>
				</labelStyle>
				<attributeRef>101</attributeRef>
				<attributeRef>102</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="8">
			<position x="60" y="-60"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-9"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>173</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="9" y="9"/>
					<textAlignment>left</textAlignment>
				</labelStyle>
				<attributeRef>103</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="9">
			<position x="60" y="-120"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-9"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>174</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="9" y="9"/>
					<textAlignment>left</textAlignment>
				</labelStyle>
				<attributeRef>104</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="10">
			<position x="60" y="-180"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-9"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>175</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="9" y="9"/>
					<textAlignment>left</textAlignment>
				</labelStyle>
				<attributeRef>105</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="11">
			<position x="60" y="-240"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-9"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>176</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="9" y="9"/>
					<textAlignment>left</textAlignment>
				</labelStyle>
				<attributeRef>106</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="12">
			<position x="60" y="-300"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-9"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>177</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="9" y="9"/>
					<textAlignment>left</textAlignment>
				</labelStyle>
				<attributeRef>107</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="13">
			<position x="120" y="-60"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-9"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>178</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="9" y="9"/>
					<textAlignment>left</textAlignment>
				</labelStyle>
				<attributeRef>108</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="14">
			<position x="120" y="-120"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-9"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>179</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="9" y="9"/>
					<textAlignment>left</textAlignment>
				</labelStyle>
				<attributeRef>109</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="15">
			<position x="120" y="-180"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-9"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>180</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="9" y="9"/>
					<textAlignment>left</textAlignment>
				</labelStyle>
				<attributeRef>110</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="16">
			<position x="120" y="-240"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-9"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>181</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="9" y="9"/>
					<textAlignment>left</textAlignment>
				</labelStyle>
				<attributeRef>111</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="17">
			<position x="120" y="-300"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-9"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>182</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="9" y="9"/>
					<textAlignment>left</textAlignment>
				</labelStyle>
				<attributeRef>112</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="18">
			<position x="180" y="-60"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-9"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>183</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="9" y="9"/>
					<textAlignment>left</textAlignment>
				</labelStyle>
				<attributeRef>113</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="19">
			<position x="180" y="-120"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-9"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>184</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="9" y="9"/>
					<textAlignment>left</textAlignment>
				</labelStyle>
				<attributeRef>114</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="20">
			<position x="180" y="-180"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-9"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>185</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="9" y="9"/>
					<textAlignment>left</textAlignment>
				</labelStyle>
				<attributeRef>115</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="21">
			<position x="180" y="-240"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-9"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>186</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="9" y="9"/>
					<textAlignment>left</textAlignment>
				</labelStyle>
				<attributeRef>116</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="22">
			<position x="180" y="-300"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-9"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>187</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="9" y="9"/>
					<textAlignment>left</textAlignment>
				</labelStyle>
				<attributeRef>117</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="23">
			<position x="-60" y="-60"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-9"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>188</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="9" y="9"/>
					<textAlignment>left</textAlignment>
				</labelStyle>
				<attributeRef>118</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="24">
			<position x="-60" y="-120"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-9"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>189</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="9" y="9"/>
					<textAlignment>left</textAlignment>
				</labelStyle>
				<attributeRef>119</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="25">
			<position x="-60" y="-180"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-9"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>190</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="9" y="9"/>
					<textAlignment>left</textAlignment>
				</labelStyle>
				<attributeRef>120</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="26">
			<position x="-60" y="-240"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-9"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>191</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="9" y="9"/>
					<textAlignment>left</textAlignment>
				</labelStyle>
				<attributeRef>121</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="27">
			<position x="-60" y="-300"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-9"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>192</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="9" y="9"/>
					<textAlignment>left</textAlignment>
				</labelStyle>
				<attributeRef>122</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="28">
			<position x="-120" y="-60"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-9"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>193</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="9" y="9"/>
					<textAlignment>left</textAlignment>
				</labelStyle>
				<attributeRef>123</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="29">
			<position x="-120" y="-120"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-9"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>194</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="9" y="9"/>
					<textAlignment>left</textAlignment>
				</labelStyle>
				<attributeRef>124</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="30">
			<position x="-120" y="-180"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-9"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>195</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="9" y="9"/>
					<textAlignment>left</textAlignment>
				</labelStyle>
				<attributeRef>125</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="31">
			<position x="-120" y="-240"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-9"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>196</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="9" y="9"/>
					<textAlignment>left</textAlignment>
				</labelStyle>
				<attributeRef>126</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="32">
			<position x="-120" y="-300"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-9"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>197</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="9" y="9"/>
					<textAlignment>left</textAlignment>
				</labelStyle>
				<attributeRef>127</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="33">
			<position x="-180" y="-120"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-9"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>198</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="9" y="9"/>
					<textAlignment>left</textAlignment>
				</labelStyle>
				<attributeRef>128</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="34">
			<position x="-180" y="-180"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-9"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>199</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="9" y="9"/>
					<textAlignment>left</textAlignment>
				</labelStyle>
				<attributeRef>129</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="35">
			<position x="-180" y="-240"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-9"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>200</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="9" y="9"/>
					<textAlignment>left</textAlignment>
				</labelStyle>
				<attributeRef>130</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="36">
			<position x="-180" y="-300"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-9"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>201</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="9" y="9"/>
					<textAlignment>left</textAlignment>
				</labelStyle>
				<attributeRef>131</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="37">
			<position x="0" y="-360"/>
			<objectContingent/>
			<attributeContingent/>
		</concept>
		<edge from="1" to="2"/>
		<edge from="1" to="7"/>
		<edge from="1" to="8"/>
		<edge from="1" to="13"/>
		<edge from="1" to="18"/>
		<edge from="1" to="23"/>
		<edge from="1" to="28"/>
		<edge from="2" to="3"/>
		<edge from="3" to="4"/>
		<edge from="4" to="5"/>
		<edge from="5" to="6"/>
		<edge from="6" to="37"/>
		<edge from="7" to="33"/>
		<edge from="8" to="9"/>
		<edge from="9" to="10"/>
		<edge from="10" to="11"/>
		<edge from="11" to="12"/>
		<edge from="12" to="37"/>
		<edge from="13" to="14"/>
		<edge from="14" to="15"/>
		<edge from="15" to="16"/>
		<edge from="16" to="17"/>
		<edge from="17" to="37"/>
		<edge from="18" to="19"/>
		<edge from="19" to="20"/>
		<edge from="20" to="21"/>
		<edge from="21" to="22"/>
		<edge from="22" to="37"/>
		<edge from="23" to="24"/>
		<edge from="24" to="25"/>
		<edge from="25" to="26"/>
		<edge from="26" to="27"/>
		<edge from="27" to="37"/>
		<edge from="28" to="29"/>
		<edge from="29" to="30"/>
		<edge from="30" to="31"/>
		<edge from="31" to="32"/>
		<edge from="32" to="37"/>
		<edge from="33" to="34"/>
		<edge from="34" to="35"/>
		<edge from="35" to="36"/>
		<edge from="36" to="37"/>
	</diagram>
	<diagram title="Graphic card">
		<concept id="1">
			<position x="-70" y="0"/>
			<objectContingent/>
			<attributeContingent/>
		</concept>
		<concept id="2">
			<position x="-280" y="-105"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-10.5"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>202</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="0" y="8.75"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<attributeRef>132</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="3">
			<position x="-280" y="-175"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-10.5"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>203</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="0" y="8.75"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<attributeRef>133</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="4">
			<position x="-70" y="-70"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-10.5"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>204</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="0" y="8.75"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<attributeRef>134</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="5">
			<position x="70" y="-140"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-10.5"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>205</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="0" y="8.75"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<attributeRef>135</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="6">
			<position x="-210" y="-140"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-10.5"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>206</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="0" y="8.75"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<attributeRef>136</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="7">
			<position x="0" y="-140"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-10.5"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>207</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="0" y="8.75"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<attributeRef>137</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="8">
			<position x="-140" y="-140"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-10.5"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>208</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="0" y="8.75"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<attributeRef>138</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="9">
			<position x="-70" y="-140"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-10.5"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>209</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="0" y="8.75"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<attributeRef>139</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="10">
			<position x="-70" y="-245"/>
			<objectContingent/>
			<attributeContingent/>
		</concept>
		<edge from="1" to="2"/>
		<edge from="1" to="4"/>
		<edge from="1" to="5"/>
		<edge from="1" to="6"/>
		<edge from="2" to="3"/>
		<edge from="3" to="10"/>
		<edge from="4" to="7"/>
		<edge from="4" to="8"/>
		<edge from="4" to="9"/>
		<edge from="5" to="10"/>
		<edge from="6" to="10"/>
		<edge from="7" to="10"/>
		<edge from="8" to="10"/>
		<edge from="9" to="10"/>
	</diagram>
	<diagram title="Ports on the mainboard">
		<concept id="1">
			<position x="-30" y="0"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-9"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>210</objectRef>
			</objectContingent>
			<attributeContingent/>
		</concept>
		<concept id="2">
			<position x="-30" y="-60"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-9"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>211</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="-6.300000000000001" y="9"/>
					<textAlignment>right</textAlignment>
				</labelStyle>
				<attributeRef>140</attributeRef>
				<attributeRef>141</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="3">
			<position x="-30" y="-120"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-9"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>212</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="0" y="10.5"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<attributeRef>142</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="4">
			<position x="30" y="-120"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-9"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>213</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="4.800000000000001" y="8.100000000000001"/>
					<textAlignment>left</textAlignment>
				</labelStyle>
				<attributeRef>143</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="5">
			<position x="30" y="-180"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-9"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>214</objectRef>
			</objectContingent>
			<attributeContingent/>
		</concept>
		<concept id="6">
			<position x="-90" y="-120"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-9"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>215</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="-3.5999999999999996" y="9.600000000000001"/>
					<textAlignment>right</textAlignment>
				</labelStyle>
				<attributeRef>144</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="7">
			<position x="-90" y="-180"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-9"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>216</objectRef>
			</objectContingent>
			<attributeContingent/>
		</concept>
		<concept id="8">
			<position x="-30" y="-240"/>
			<objectContingent/>
			<attributeContingent/>
		</concept>
		<edge from="1" to="2"/>
		<edge from="2" to="3"/>
		<edge from="2" to="4"/>
		<edge from="2" to="6"/>
		<edge from="3" to="5"/>
		<edge from="3" to="7"/>
		<edge from="4" to="5"/>
		<edge from="5" to="8"/>
		<edge from="6" to="7"/>
		<edge from="7" to="8"/>
	</diagram>
	<diagram title="Internal drive bays">
		<concept id="1">
			<position x="0" y="0"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-12"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>217</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="-11.6" y="13.6"/>
					<textAlignment>right</textAlignment>
				</labelStyle>
				<attributeRef>145</attributeRef>
				<attributeRef>146</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="2">
			<position x="-40" y="-40"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-12"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>218</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="-14" y="12"/>
					<textAlignment>right</textAlignment>
				</labelStyle>
				<attributeRef>147</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="3">
			<position x="-80" y="-80"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-12"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>219</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="-10.4" y="14"/>
					<textAlignment>right</textAlignment>
				</labelStyle>
				<attributeRef>148</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="4">
			<position x="-120" y="-120"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-12"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>220</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="-11.2" y="13.6"/>
					<textAlignment>right</textAlignment>
				</labelStyle>
				<attributeRef>149</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="5">
			<position x="40" y="-40"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-12"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>221</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="12" y="12"/>
					<textAlignment>left</textAlignment>
				</labelStyle>
				<attributeRef>150</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="6">
			<position x="0" y="-80"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-12"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>222</objectRef>
			</objectContingent>
			<attributeContingent/>
		</concept>
		<concept id="7">
			<position x="-40" y="-120"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-12"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>223</objectRef>
			</objectContingent>
			<attributeContingent/>
		</concept>
		<concept id="8">
			<position x="-80" y="-160"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-12"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>224</objectRef>
			</objectContingent>
			<attributeContingent/>
		</concept>
		<concept id="9">
			<position x="80" y="-80"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-12"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>225</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="12" y="12"/>
					<textAlignment>left</textAlignment>
				</labelStyle>
				<attributeRef>151</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="10">
			<position x="40" y="-120"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-12"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>226</objectRef>
			</objectContingent>
			<attributeContingent/>
		</concept>
		<concept id="11">
			<position x="0" y="-160"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-12"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>227</objectRef>
			</objectContingent>
			<attributeContingent/>
		</concept>
		<concept id="12">
			<position x="-40" y="-200"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-12"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>228</objectRef>
			</objectContingent>
			<attributeContingent/>
		</concept>
		<concept id="13">
			<position x="120" y="-120"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-12"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>229</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="12" y="12"/>
					<textAlignment>left</textAlignment>
				</labelStyle>
				<attributeRef>152</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="14">
			<position x="80" y="-160"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-12"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>230</objectRef>
			</objectContingent>
			<attributeContingent/>
		</concept>
		<concept id="15">
			<position x="40" y="-200"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-12"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>231</objectRef>
			</objectContingent>
			<attributeContingent/>
		</concept>
		<concept id="16">
			<position x="0" y="-240"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-12"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>232</objectRef>
			</objectContingent>
			<attributeContingent/>
		</concept>
		<concept id="17">
			<position x="-160" y="-160"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-12"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>233</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="-12" y="13.2"/>
					<textAlignment>right</textAlignment>
				</labelStyle>
				<attributeRef>153</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="18">
			<position x="-120" y="-200"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-12"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>234</objectRef>
			</objectContingent>
			<attributeContingent/>
		</concept>
		<concept id="19">
			<position x="-80" y="-240"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-12"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>235</objectRef>
			</objectContingent>
			<attributeContingent/>
		</concept>
		<concept id="20">
			<position x="-40" y="-280"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-12"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>236</objectRef>
			</objectContingent>
			<attributeContingent/>
		</concept>
		<concept id="21">
			<position x="160" y="-160"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-12"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>237</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="12" y="12"/>
					<textAlignment>left</textAlignment>
				</labelStyle>
				<attributeRef>154</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="22">
			<position x="120" y="-200"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-12"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>238</objectRef>
			</objectContingent>
			<attributeContingent/>
		</concept>
		<concept id="23">
			<position x="80" y="-240"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-12"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>239</objectRef>
			</objectContingent>
			<attributeContingent/>
		</concept>
		<concept id="24">
			<position x="40" y="-280"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-12"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>240</objectRef>
			</objectContingent>
			<attributeContingent/>
		</concept>
		<concept id="25">
			<position x="0" y="-320"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-12"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>241</objectRef>
			</objectContingent>
			<attributeContingent/>
		</concept>
		<edge from="1" to="2"/>
		<edge from="1" to="5"/>
		<edge from="2" to="3"/>
		<edge from="2" to="6"/>
		<edge from="3" to="4"/>
		<edge from="3" to="7"/>
		<edge from="4" to="8"/>
		<edge from="4" to="17"/>
		<edge from="5" to="6"/>
		<edge from="5" to="9"/>
		<edge from="6" to="7"/>
		<edge from="6" to="10"/>
		<edge from="11" to="12"/>
		<edge from="11" to="15"/>
		<edge from="12" to="16"/>
		<edge from="12" to="19"/>
		<edge from="13" to="14"/>
		<edge from="13" to="21"/>
		<edge from="14" to="15"/>
		<edge from="14" to="22"/>
		<edge from="15" to="16"/>
		<edge from="15" to="23"/>
		<edge from="16" to="20"/>
		<edge from="16" to="24"/>
		<edge from="17" to="18"/>
		<edge from="18" to="19"/>
		<edge from="19" to="20"/>
		<edge from="20" to="25"/>
		<edge from="21" to="22"/>
		<edge from="22" to="23"/>
		<edge from="23" to="24"/>
		<edge from="24" to="25"/>
	</diagram>
</conceptualSchema>
