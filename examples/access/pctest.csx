<?xml version="1.0" encoding="UTF-8"?>
<!-- This file is based on the Toscana2/3 example PCTest20/PCTest80 and has been converted by Toscana 3,
	afterwards the diagrams have been scaled to acommodate with the fact that nodes and lines now have
	dimension in the model coordinate system.
	
	Here are the parameters to scale the diagrams from the original CSC->CSX conversion to this file:
	
        <xsl:param name="diagram">Leistung des Netzteils und Zahl der Anschlüsse</xsl:param>
        <xsl:param name="scale">3</xsl:param>
        <xsl:param name="diagram">Graphikkarte</xsl:param>
        <xsl:param name="scale">3.5</xsl:param>
        <xsl:param name="diagram">Zugängliche Laufwerke</xsl:param>
        <xsl:param name="scale">5</xsl:param>
        <xsl:param name="diagram">Bustypen der 486/66 PCs</xsl:param>
        <xsl:param name="scale">4</xsl:param>
        <xsl:param name="diagram">WinMarks (Graphics/Disk) für 486/66 PCs</xsl:param>
        <xsl:param name="scale">4</xsl:param>
        <xsl:param name="diagram">Festplattengrößen (ordinal)</xsl:param>
        <xsl:param name="scale">5</xsl:param>
        <xsl:param name="diagram">DOS-Win-Mark</xsl:param>
        <xsl:param name="scale">4</xsl:param>
        <xsl:param name="diagram">Vertriebsform</xsl:param>
        <xsl:param name="scale">7</xsl:param>
        <xsl:param name="diagram">Gehäusetyp</xsl:param>
        <xsl:param name="scale">2.5</xsl:param>
        <xsl:param name="diagram">Ports auf dem Motherboard</xsl:param>
        <xsl:param name="scale">3</xsl:param>
        <xsl:param name="diagram">interne Laufwerksschächte</xsl:param>
        <xsl:param name="scale">4</xsl:param>
-->
<conceptualSchema version="TJ0.4">
	<context>
        <databaseConnection>
            <url driver="sun.jdbc.odbc.JdbcOdbcDriver">jdbc:odbc:PCTest80</url>
            <table>[PC-Test]</table>
            <key>[PCBezeichnung]</key>
            <queries dropDefaults="false">
                <listQuery name="Cases" distinct="true">
                    <column>[Gehäusetyp]</column>
                </listQuery>
                <listQuery name="Name with type of case (SQL)">
                    <column>[Gehäusetyp] + ': ' + [PCBezeichnung]</column>
                </listQuery>
                <listQuery name="Name with type of case (Toscana)" head="Case: ">
                    <column name="Case" separator=" - PC: ">[Gehäusetyp]</column>
                    <column name="Name">[PCBezeichnung]</column>
                </listQuery>
                <aggregateQuery name="Average Price">
                    <column format="$ 0.00">AVG(Preis)</column>
                </aggregateQuery>
                <aggregateQuery name="Min/Max Prices">
                    <column format="$0.00" separator=" - ">MIN(Preis)</column>
                    <column format="$0.00">MAX(Preis)</column>
                </aggregateQuery>
            </queries>
        </databaseConnection>
		<object id="1">Preis&gt;=5000</object>
		<object id="2">Preis&lt;5000 and Preis&gt;=4500</object>
		<object id="3">Preis&lt;4500 and Preis&gt;=4000</object>
		<object id="4">Preis&lt;4000 and Preis&gt;=3500</object>
		<object id="5">Preis&lt;3500 and Preis&gt;=3000</object>
		<object id="6">Preis&lt;3000 and Preis&gt;=2500</object>
		<object id="7">Preis&lt;2500</object>
		<object id="8">Festplatte&gt;=500</object>
		<object id="9">Festplatte&lt;500 and Festplatte&gt;=450</object>
		<object id="10">Festplatte&lt;450 and Festplatte&gt;=400</object>
		<object id="11">Festplatte&lt;400 and Festplatte&gt;=350</object>
		<object id="12">Festplatte&lt;350 and Festplatte&gt;=300</object>
		<object id="13">Festplatte&lt;250 and Festplatte&gt;=225</object>
		<object id="14">Festplatte&lt;225</object>
		<object id="15">DISKmark&gt;0 and DISKmark&lt;=15 AND Graphics&gt;0 and Graphics&lt;=5</object>
		<object id="16">DISKmark&gt;0 and DISKmark&lt;=15 AND Graphics&gt;5 and Graphics&lt;=10</object>
		<object id="17">DISKmark&gt;0 and DISKmark&lt;=15 AND Graphics&gt;10 and Graphics&lt;=15</object>
		<object id="18">DISKmark&gt;0 and DISKmark&lt;=15 AND Graphics&gt;15 and Graphics&lt;=20</object>
		<object id="19">DISKmark&gt;0 and DISKmark&lt;=15 AND Graphics&gt;20 and Graphics&lt;=30</object>
		<object id="20">DISKmark&gt;0 and DISKmark&lt;=15 AND Graphics&gt;30 and Graphics&lt;=40</object>
		<object id="21">DISKmark&gt;0 and DISKmark&lt;=15 AND Graphics&gt;40</object>
		<object id="22">DISKmark&gt;15 and DISKmark&lt;=30 AND Graphics&gt;0 and Graphics&lt;=5</object>
		<object id="23">DISKmark&gt;30 and DISKmark&lt;=45 AND Graphics&gt;0 and Graphics&lt;=5</object>
		<object id="24">DISKmark&gt;45 and DISKmark&lt;=60 AND Graphics&gt;0 and Graphics&lt;=5</object>
		<object id="25">DISKmark&gt;60 and DISKmark&lt;=90 AND Graphics&gt;0 and Graphics&lt;=5</object>
		<object id="26">DISKmark&gt;90 and DISKmark&lt;=120 AND Graphics&gt;0 and Graphics&lt;=5</object>
		<object id="27">DISKmark&gt;120 AND Graphics&gt;0 and Graphics&lt;=5</object>
		<object id="28">DISKmark&gt;15 and DISKmark&lt;=30 AND Graphics&gt;5 and Graphics&lt;=10</object>
		<object id="29">DISKmark&gt;15 and DISKmark&lt;=30 AND Graphics&gt;10 and Graphics&lt;=15</object>
		<object id="30">DISKmark&gt;15 and DISKmark&lt;=30 AND Graphics&gt;15 and Graphics&lt;=20</object>
		<object id="31">DISKmark&gt;15 and DISKmark&lt;=30 AND Graphics&gt;20 and Graphics&lt;=30</object>
		<object id="32">DISKmark&gt;15 and DISKmark&lt;=30 AND Graphics&gt;30 and Graphics&lt;=40</object>
		<object id="33">DISKmark&gt;15 and DISKmark&lt;=30 AND Graphics&gt;40</object>
		<object id="34">DISKmark&gt;30 and DISKmark&lt;=45 AND Graphics&gt;5 and Graphics&lt;=10</object>
		<object id="35">DISKmark&gt;45 and DISKmark&lt;=60 AND Graphics&gt;5 and Graphics&lt;=10</object>
		<object id="36">DISKmark&gt;60 and DISKmark&lt;=90 AND Graphics&gt;5 and Graphics&lt;=10</object>
		<object id="37">DISKmark&gt;90 and DISKmark&lt;=120 AND Graphics&gt;5 and Graphics&lt;=10</object>
		<object id="38">DISKmark&gt;120 AND Graphics&gt;5 and Graphics&lt;=10</object>
		<object id="39">DISKmark&gt;30 and DISKmark&lt;=45 AND Graphics&gt;10 and Graphics&lt;=15</object>
		<object id="40">DISKmark&gt;30 and DISKmark&lt;=45 AND Graphics&gt;15 and Graphics&lt;=20</object>
		<object id="41">DISKmark&gt;30 and DISKmark&lt;=45 AND Graphics&gt;20 and Graphics&lt;=30</object>
		<object id="42">DISKmark&gt;30 and DISKmark&lt;=45 AND Graphics&gt;30 and Graphics&lt;=40</object>
		<object id="43">DISKmark&gt;30 and DISKmark&lt;=45 AND Graphics&gt;40</object>
		<object id="44">DISKmark&gt;45 and DISKmark&lt;=60 AND Graphics&gt;10 and Graphics&lt;=15</object>
		<object id="45">DISKmark&gt;60 and DISKmark&lt;=90 AND Graphics&gt;10 and Graphics&lt;=15</object>
		<object id="46">DISKmark&gt;90 and DISKmark&lt;=120 AND Graphics&gt;10 and Graphics&lt;=15</object>
		<object id="47">DISKmark&gt;120 AND Graphics&gt;10 and Graphics&lt;=15</object>
		<object id="48">DISKmark&gt;45 and DISKmark&lt;=60 AND Graphics&gt;15 and Graphics&lt;=20</object>
		<object id="49">DISKmark&gt;45 and DISKmark&lt;=60 AND Graphics&gt;20 and Graphics&lt;=30</object>
		<object id="50">DISKmark&gt;45 and DISKmark&lt;=60 AND Graphics&gt;30 and Graphics&lt;=40</object>
		<object id="51">DISKmark&gt;45 and DISKmark&lt;=60 AND Graphics&gt;40</object>
		<object id="52">DISKmark&gt;60 and DISKmark&lt;=90 AND Graphics&gt;15 and Graphics&lt;=20</object>
		<object id="53">DISKmark&gt;90 and DISKmark&lt;=120 AND Graphics&gt;15 and Graphics&lt;=20</object>
		<object id="54">DISKmark&gt;120 AND Graphics&gt;15 and Graphics&lt;=20</object>
		<object id="55">DISKmark&gt;60 and DISKmark&lt;=90 AND Graphics&gt;20 and Graphics&lt;=30</object>
		<object id="56">DISKmark&gt;60 and DISKmark&lt;=90 AND Graphics&gt;30 and Graphics&lt;=40</object>
		<object id="57">DISKmark&gt;60 and DISKmark&lt;=90 AND Graphics&gt;40</object>
		<object id="58">DISKmark&gt;90 and DISKmark&lt;=120 AND Graphics&gt;20 and Graphics&lt;=30</object>
		<object id="59">DISKmark&gt;120 AND Graphics&gt;20 and Graphics&lt;=30</object>
		<object id="60">DISKmark&gt;90 and DISKmark&lt;=120 AND Graphics&gt;30 and Graphics&lt;=40</object>
		<object id="61">DISKmark&gt;90 and DISKmark&lt;=120 AND Graphics&gt;40</object>
		<object id="62">DISKmark&gt;120 AND Graphics&gt;30 and Graphics&lt;=40</object>
		<object id="63">DISKmark&gt;120 AND Graphics&gt;40</object>
		<object id="64"/>
		<object id="65">Bustyp='ISA'</object>
		<object id="66">Bustyp='EISA'</object>
		<object id="67">Bustyp='MCA'</object>
		<object id="68">Festplatte &lt; 200</object>
		<object id="69">Festplatte Between 200 And 399</object>
		<object id="70">Festplatte &gt;= 400</object>
		<object id="71">Video&gt;=9000</object>
		<object id="72">Video between 7500 and 8999</object>
		<object id="73">Video between 6000 and 7499</object>
		<object id="74">Video between 4500 and 5999</object>
		<object id="75">Video between 3000 and 4499</object>
		<object id="76">Video between 1500 and 2999</object>
		<object id="77">Video&lt;1500</object>
		<object id="78">DISKmark&gt;0 and DISKmark&lt;=15 AND DOSmark&gt;=0 and DOSmark&lt;40</object>
		<object id="79">DISKmark&gt;0 and DISKmark&lt;=15 AND DOSmark&gt;=40 and DOSmark&lt;50</object>
		<object id="80">DISKmark&gt;0 and DISKmark&lt;=15 AND DOSmark&gt;=50 and DOSmark&lt;60</object>
		<object id="81">DISKmark&gt;0 and DISKmark&lt;=15 AND DOSmark&gt;=60 and DOSmark&lt;70</object>
		<object id="82">DISKmark&gt;0 and DISKmark&lt;=15 AND DOSmark&gt;=70 and DOSmark&lt;80</object>
		<object id="83">DISKmark&gt;0 and DISKmark&lt;=15 AND DOSmark&gt;=80 and DOSmark&lt;90</object>
		<object id="84">DISKmark&gt;0 and DISKmark&lt;=15 AND DOSmark&gt;=90</object>
		<object id="85">DISKmark&gt;15 and DISKmark&lt;=30 AND DOSmark&gt;=0 and DOSmark&lt;40</object>
		<object id="86">DISKmark&gt;30 and DISKmark&lt;=45 AND DOSmark&gt;=0 and DOSmark&lt;40</object>
		<object id="87">DISKmark&gt;45 and DISKmark&lt;=60 AND DOSmark&gt;=0 and DOSmark&lt;40</object>
		<object id="88">DISKmark&gt;60 and DISKmark&lt;=90 AND DOSmark&gt;=0 and DOSmark&lt;40</object>
		<object id="89">DISKmark&gt;90 and DISKmark&lt;=120 AND DOSmark&gt;=0 and DOSmark&lt;40</object>
		<object id="90">DISKmark&gt;120 AND DOSmark&gt;=0 and DOSmark&lt;40</object>
		<object id="91">DISKmark&gt;15 and DISKmark&lt;=30 AND DOSmark&gt;=40 and DOSmark&lt;50</object>
		<object id="92">DISKmark&gt;15 and DISKmark&lt;=30 AND DOSmark&gt;=50 and DOSmark&lt;60</object>
		<object id="93">DISKmark&gt;15 and DISKmark&lt;=30 AND DOSmark&gt;=60 and DOSmark&lt;70</object>
		<object id="94">DISKmark&gt;15 and DISKmark&lt;=30 AND DOSmark&gt;=70 and DOSmark&lt;80</object>
		<object id="95">DISKmark&gt;15 and DISKmark&lt;=30 AND DOSmark&gt;=80 and DOSmark&lt;90</object>
		<object id="96">DISKmark&gt;15 and DISKmark&lt;=30 AND DOSmark&gt;=90</object>
		<object id="97">DISKmark&gt;30 and DISKmark&lt;=45 AND DOSmark&gt;=40 and DOSmark&lt;50</object>
		<object id="98">DISKmark&gt;45 and DISKmark&lt;=60 AND DOSmark&gt;=40 and DOSmark&lt;50</object>
		<object id="99">DISKmark&gt;60 and DISKmark&lt;=90 AND DOSmark&gt;=40 and DOSmark&lt;50</object>
		<object id="100">DISKmark&gt;90 and DISKmark&lt;=120 AND DOSmark&gt;=40 and DOSmark&lt;50</object>
		<object id="101">DISKmark&gt;120 AND DOSmark&gt;=40 and DOSmark&lt;50</object>
		<object id="102">DISKmark&gt;30 and DISKmark&lt;=45 AND DOSmark&gt;=50 and DOSmark&lt;60</object>
		<object id="103">DISKmark&gt;30 and DISKmark&lt;=45 AND DOSmark&gt;=60 and DOSmark&lt;70</object>
		<object id="104">DISKmark&gt;30 and DISKmark&lt;=45 AND DOSmark&gt;=70 and DOSmark&lt;80</object>
		<object id="105">DISKmark&gt;30 and DISKmark&lt;=45 AND DOSmark&gt;=80 and DOSmark&lt;90</object>
		<object id="106">DISKmark&gt;30 and DISKmark&lt;=45 AND DOSmark&gt;=90</object>
		<object id="107">DISKmark&gt;45 and DISKmark&lt;=60 AND DOSmark&gt;=50 and DOSmark&lt;60</object>
		<object id="108">DISKmark&gt;60 and DISKmark&lt;=90 AND DOSmark&gt;=50 and DOSmark&lt;60</object>
		<object id="109">DISKmark&gt;90 and DISKmark&lt;=120 AND DOSmark&gt;=50 and DOSmark&lt;60</object>
		<object id="110">DISKmark&gt;120 AND DOSmark&gt;=50 and DOSmark&lt;60</object>
		<object id="111">DISKmark&gt;45 and DISKmark&lt;=60 AND DOSmark&gt;=60 and DOSmark&lt;70</object>
		<object id="112">DISKmark&gt;45 and DISKmark&lt;=60 AND DOSmark&gt;=70 and DOSmark&lt;80</object>
		<object id="113">DISKmark&gt;45 and DISKmark&lt;=60 AND DOSmark&gt;=80 and DOSmark&lt;90</object>
		<object id="114">DISKmark&gt;45 and DISKmark&lt;=60 AND DOSmark&gt;=90</object>
		<object id="115">DISKmark&gt;60 and DISKmark&lt;=90 AND DOSmark&gt;=60 and DOSmark&lt;70</object>
		<object id="116">DISKmark&gt;90 and DISKmark&lt;=120 AND DOSmark&gt;=60 and DOSmark&lt;70</object>
		<object id="117">DISKmark&gt;120 AND DOSmark&gt;=60 and DOSmark&lt;70</object>
		<object id="118">DISKmark&gt;60 and DISKmark&lt;=90 AND DOSmark&gt;=70 and DOSmark&lt;80</object>
		<object id="119">DISKmark&gt;60 and DISKmark&lt;=90 AND DOSmark&gt;=80 and DOSmark&lt;90</object>
		<object id="120">DISKmark&gt;60 and DISKmark&lt;=90 AND DOSmark&gt;=90</object>
		<object id="121">DISKmark&gt;90 and DISKmark&lt;=120 AND DOSmark&gt;=70 and DOSmark&lt;80</object>
		<object id="122">DISKmark&gt;120 AND DOSmark&gt;=70 and DOSmark&lt;80</object>
		<object id="123">DISKmark&gt;90 and DISKmark&lt;=120 AND DOSmark&gt;=80 and DOSmark&lt;90</object>
		<object id="124">DISKmark&gt;90 and DISKmark&lt;=120 AND DOSmark&gt;=90</object>
		<object id="125">DISKmark&gt;120 AND DOSmark&gt;=80 and DOSmark&lt;90</object>
		<object id="126">DISKmark&gt;120 AND DOSmark&gt;=90</object>
		<object id="127">Direktvertrieb=Yes and [Händler]=No</object>
		<object id="128">Direktvertrieb=No and [Händler]=Yes</object>
		<object id="129">Direktvertrieb=Yes and [Händler]=Yes</object>
		<object id="130">[Gehäusetyp] = 'Desktop'</object>
		<object id="131">[Gehäusetyp] = 'Slimline'</object>
		<object id="132">[Gehäusetyp] = 'Small-footprint'</object>
		<object id="133">[Gehäusetyp] = 'Tower'</object>
		<object id="134">[Gehäusetyp] = 'Minitower'</object>
		<object id="135">[Freie Laufwerksschächte] = 0</object>
		<object id="136">[Freie Laufwerksschächte] = 1</object>
		<object id="137">[Freie Laufwerksschächte] = 2</object>
		<object id="138">[Freie Laufwerksschächte] = 3</object>
		<object id="139">[Freie Laufwerksschächte] = 10</object>
		<object id="140">[Freie Laufwerksschächte] = 11</object>
		<object id="141">[Freie Laufwerksschächte] = 12</object>
		<object id="142">[Freie Laufwerksschächte] = 13</object>
		<object id="143">[Freie Laufwerksschächte] = 20</object>
		<object id="144">[Freie Laufwerksschächte] = 21</object>
		<object id="145">[Freie Laufwerksschächte] = 22</object>
		<object id="146">[Freie Laufwerksschächte] = 23</object>
		<object id="147">[Freie Laufwerksschächte] = 30</object>
		<object id="148">[Freie Laufwerksschächte] = 31</object>
		<object id="149">[Freie Laufwerksschächte] = 32</object>
		<object id="150">[Freie Laufwerksschächte] = 33</object>
		<object id="151">[Freie Laufwerksschächte] = 40</object>
		<object id="152">[Freie Laufwerksschächte] = 41</object>
		<object id="153">[Freie Laufwerksschächte] = 42</object>
		<object id="154">[Freie Laufwerksschächte] = 43</object>
		<object id="155">[Freie Laufwerksschächte] = 50</object>
		<object id="156">[Freie Laufwerksschächte] = 51</object>
		<object id="157">[Freie Laufwerksschächte] = 52</object>
		<object id="158">[Freie Laufwerksschächte] = 53</object>
		<object id="159">[Freie Laufwerksschächte] = 60</object>
		<object id="160">[Freie Laufwerksschächte] = 61</object>
		<object id="161">[Freie Laufwerksschächte] = 62</object>
		<object id="162">[Freie Laufwerksschächte] = 63</object>
		<object id="163">[Freie Laufwerksschächte] = 70</object>
		<object id="164">[Freie Laufwerksschächte] = 71</object>
		<object id="165">[Freie Laufwerksschächte] = 72</object>
		<object id="166">[Freie Laufwerksschächte] = 73</object>
		<object id="167">Netzteil&lt;150 and [Netzteilanschlüsse]=5</object>
		<object id="168">Netzteil between 150 and 199 and [Netzteilanschlüsse]=5</object>
		<object id="169">Netzteil between 200 and 249 and [Netzteilanschlüsse]=5</object>
		<object id="170">Netzteil between 250 and 299 and [Netzteilanschlüsse]=5</object>
		<object id="171">Netzteil&gt;=300 and [Netzteilanschlüsse]=5</object>
		<object id="172">Netzteil&lt;150 and [Netzteilanschlüsse]=2</object>
		<object id="173">Netzteil&lt;150 and [Netzteilanschlüsse]=6</object>
		<object id="174">Netzteil between 150 and 199 and [Netzteilanschlüsse]=6</object>
		<object id="175">Netzteil between 200 and 249 and [Netzteilanschlüsse]=6</object>
		<object id="176">Netzteil between 250 and 299 and [Netzteilanschlüsse]=6</object>
		<object id="177">Netzteil&gt;=300 and [Netzteilanschlüsse]=6</object>
		<object id="178">Netzteil&lt;150 and [Netzteilanschlüsse]=7</object>
		<object id="179">Netzteil between 150 and 199 and [Netzteilanschlüsse]=7</object>
		<object id="180">Netzteil between 200 and 249 and [Netzteilanschlüsse]=7</object>
		<object id="181">Netzteil between 250 and 299 and [Netzteilanschlüsse]=7</object>
		<object id="182">Netzteil&gt;=300 and [Netzteilanschlüsse]=7</object>
		<object id="183">Netzteil&lt;150 and [Netzteilanschlüsse]=8</object>
		<object id="184">Netzteil between 150 and 199 and [Netzteilanschlüsse]=8</object>
		<object id="185">Netzteil between 200 and 249 and [Netzteilanschlüsse]=8</object>
		<object id="186">Netzteil between 250 and 299 and [Netzteilanschlüsse]=8</object>
		<object id="187">Netzteil&gt;=300 and [Netzteilanschlüsse]=8</object>
		<object id="188">Netzteil&lt;150 and [Netzteilanschlüsse]=4</object>
		<object id="189">Netzteil between 150 and 199 and [Netzteilanschlüsse]=4</object>
		<object id="190">Netzteil between 200 and 249 and [Netzteilanschlüsse]=4</object>
		<object id="191">Netzteil between 250 and 299 and [Netzteilanschlüsse]=4</object>
		<object id="192">Netzteil&gt;=300 and [Netzteilanschlüsse]=4</object>
		<object id="193">Netzteil&lt;150 and [Netzteilanschlüsse]=3</object>
		<object id="194">Netzteil between 150 and 199 and [Netzteilanschlüsse]=3</object>
		<object id="195">Netzteil between 200 and 249 and [Netzteilanschlüsse]=3</object>
		<object id="196">Netzteil between 250 and 299 and [Netzteilanschlüsse]=3</object>
		<object id="197">Netzteil&gt;=300 and [Netzteilanschlüsse]=3</object>
		<object id="198">Netzteil between 150 and 199 and [Netzteilanschlüsse]=2</object>
		<object id="199">Netzteil between 200 and 249 and [Netzteilanschlüsse]=2</object>
		<object id="200">Netzteil between 250 and 299 and [Netzteilanschlüsse]=2</object>
		<object id="201">Netzteil&gt;=300 and [Netzteilanschlüsse]=2</object>
		<object id="202">Graphikkarte = 'ISA'</object>
		<object id="203">Graphikkarte = 'EISA'</object>
		<object id="204">Graphikkarte = 'Local Bus'</object>
		<object id="205">Graphikkarte = 'Motherboard'</object>
		<object id="206">Graphikkarte = 'MCA'</object>
		<object id="207">Graphikkarte = 'VESA Local Bus'</object>
		<object id="208">Graphikkarte = 'Proprietary Local Bus'</object>
		<object id="209">Graphikkarte = 'UBSA Local Bus'</object>
		<object id="210">Ports = 'Keine'</object>
		<object id="211">Ports = '1,1,0'</object>
		<object id="212">Ports = '1,1,1'</object>
		<object id="213">Ports = '1,2,0'</object>
		<object id="214">Ports = '1,2,1'</object>
		<object id="215">Ports = '2,1,0'</object>
		<object id="216">Ports = '2,1,1'</object>
		<object id="217">[interne Laufwerkschächte] = 0</object>
		<object id="218">[interne Laufwerkschächte] = 10</object>
		<object id="219">[interne Laufwerkschächte] = 20</object>
		<object id="220">[interne Laufwerkschächte] = 30</object>
		<object id="221">[interne Laufwerkschächte] = 1</object>
		<object id="222">[interne Laufwerkschächte] = 11</object>
		<object id="223">[interne Laufwerkschächte] = 21</object>
		<object id="224">[interne Laufwerkschächte] = 31</object>
		<object id="225">[interne Laufwerkschächte] = 2</object>
		<object id="226">[interne Laufwerkschächte] = 12</object>
		<object id="227">[interne Laufwerkschächte] = 22</object>
		<object id="228">[interne Laufwerkschächte] = 32</object>
		<object id="229">[interne Laufwerkschächte] = 3</object>
		<object id="230">[interne Laufwerkschächte] = 13</object>
		<object id="231">[interne Laufwerkschächte] = 23</object>
		<object id="232">[interne Laufwerkschächte] = 33</object>
		<object id="233">[interne Laufwerkschächte] = 40</object>
		<object id="234">[interne Laufwerkschächte] = 41</object>
		<object id="235">[interne Laufwerkschächte] = 42</object>
		<object id="236">[interne Laufwerkschächte] = 43</object>
		<object id="237">[interne Laufwerkschächte] = 4</object>
		<object id="238">[interne Laufwerkschächte] = 14</object>
		<object id="239">[interne Laufwerkschächte] = 24</object>
		<object id="240">[interne Laufwerkschächte] = 34</object>
		<object id="241">[interne Laufwerkschächte] = 44</object>
		<attribute id="1"/>
		<attribute id="2"/>
		<attribute id="3" name="&gt;=2500$"/>
		<attribute id="4" name="&gt;=3000$"/>
		<attribute id="5" name="&gt;=3500$"/>
		<attribute id="6" name="&gt;=4000$"/>
		<attribute id="7" name="&gt;=4500$"/>
		<attribute id="8" name="&gt;=5000$"/>
		<attribute id="9" name="&lt;5000$"/>
		<attribute id="10" name="&lt;4500$"/>
		<attribute id="11" name="&lt;4000$"/>
		<attribute id="12" name="&lt;3500$"/>
		<attribute id="13" name="&lt;3000$"/>
		<attribute id="14" name="&lt;2500$"/>
		<attribute id="15"/>
		<attribute id="16"/>
		<attribute id="17" name="&gt;=225MB"/>
		<attribute id="18" name="&gt;=250MB"/>
		<attribute id="19" name="&gt;=350MB"/>
		<attribute id="20" name="&gt;=400MB"/>
		<attribute id="21" name="&gt;=450MB"/>
		<attribute id="22" name="&gt;=500MB"/>
		<attribute id="23" name="&lt;500MB"/>
		<attribute id="24" name="&lt;450MB"/>
		<attribute id="25" name="&lt;400MB"/>
		<attribute id="26" name="&lt;350MB"/>
		<attribute id="27" name="&lt;250MB"/>
		<attribute id="28" name="&lt;225MB"/>
		<attribute id="29" name="Disk WinMark &gt; 0, Graphics WinMark &gt; 0"/>
		<attribute id="30" name="&gt; 5"/>
		<attribute id="31" name="&gt; 10"/>
		<attribute id="32" name="&gt; 15"/>
		<attribute id="33" name="&gt; 20"/>
		<attribute id="34" name="&gt; 30"/>
		<attribute id="35" name="&gt; 40"/>
		<attribute id="36" name="&gt; 15"/>
		<attribute id="37" name="&gt; 30"/>
		<attribute id="38" name="&gt; 45"/>
		<attribute id="39" name="&gt; 60"/>
		<attribute id="40" name="&gt; 90"/>
		<attribute id="41" name="&gt; 120"/>
		<attribute id="42" name="ISA-Bus"/>
		<attribute id="43" name="EISA-Bus"/>
		<attribute id="44" name="MCA-Bus"/>
		<attribute id="45" name="&gt;=200MB"/>
		<attribute id="46" name="&gt;=400MB"/>
		<attribute id="47"/>
		<attribute id="48"/>
		<attribute id="49" name="&gt;=1500"/>
		<attribute id="50" name="&gt;=3000"/>
		<attribute id="51" name="&gt;=4500"/>
		<attribute id="52" name="&gt;=6000"/>
		<attribute id="53" name="&gt;=7500"/>
		<attribute id="54" name="&gt;=9000"/>
		<attribute id="55" name="&lt;9000"/>
		<attribute id="56" name="&lt;7500"/>
		<attribute id="57" name="&lt;6000"/>
		<attribute id="58" name="&lt;4500"/>
		<attribute id="59" name="&lt;3000"/>
		<attribute id="60" name="&lt;1500"/>
		<attribute id="61" name="Disk WinMark &gt; 0, DOSmark &gt;= 0"/>
		<attribute id="62" name="&gt;= 40"/>
		<attribute id="63" name="&gt;= 50"/>
		<attribute id="64" name="&gt;= 60"/>
		<attribute id="65" name="&gt;= 70"/>
		<attribute id="66" name="&gt;= 80"/>
		<attribute id="67" name="&gt;= 90"/>
		<attribute id="68" name="&gt; 15"/>
		<attribute id="69" name="&gt; 30"/>
		<attribute id="70" name="&gt; 45"/>
		<attribute id="71" name="&gt; 60"/>
		<attribute id="72" name="&gt; 90"/>
		<attribute id="73" name="&gt; 120"/>
		<attribute id="74" name="Direktvertrieb"/>
		<attribute id="75" name="Nur Direktvertrieb"/>
		<attribute id="76" name="Händler"/>
		<attribute id="77" name="Nur Händlervertrieb"/>
		<attribute id="78" name="Beide Vertriebsformen"/>
		<attribute id="79" name="Desktop"/>
		<attribute id="80" name="Slimline"/>
		<attribute id="81" name="Small-footprint"/>
		<attribute id="82" name="Tower"/>
		<attribute id="83" name="Mini-Tower"/>
		<attribute id="84" name="kein 5¼&quot; Schacht"/>
		<attribute id="85" name="kein 3½&quot; Schacht"/>
		<attribute id="86" name="ein 3½&quot; Schacht"/>
		<attribute id="87" name="zwei 3½&quot; Schächte"/>
		<attribute id="88" name="drei 3½&quot; Schächte"/>
		<attribute id="89" name="ein 5¼&quot; Schacht"/>
		<attribute id="90" name="zwei 5¼&quot; Schächte"/>
		<attribute id="91" name="drei 5¼&quot; Schächte"/>
		<attribute id="92" name="vier 5¼&quot; Schächte"/>
		<attribute id="93" name="fünf 5¼&quot; Schächte"/>
		<attribute id="94" name="sechs 5¼&quot; Schächte"/>
		<attribute id="95" name="sieben 5¼&quot; Schächte"/>
		<attribute id="96" name="5 Anschlüsse"/>
		<attribute id="97"/>
		<attribute id="98"/>
		<attribute id="99"/>
		<attribute id="100"/>
		<attribute id="101" name="Leistung &lt;150 Watt"/>
		<attribute id="102" name="2 Anschlüsse"/>
		<attribute id="103" name="6 Anschlüsse"/>
		<attribute id="104"/>
		<attribute id="105"/>
		<attribute id="106"/>
		<attribute id="107"/>
		<attribute id="108" name="7 Anschlüsse"/>
		<attribute id="109"/>
		<attribute id="110"/>
		<attribute id="111"/>
		<attribute id="112"/>
		<attribute id="113" name="8 Anschlüsse"/>
		<attribute id="114"/>
		<attribute id="115"/>
		<attribute id="116"/>
		<attribute id="117"/>
		<attribute id="118" name="4 Anschlüsse"/>
		<attribute id="119"/>
		<attribute id="120"/>
		<attribute id="121"/>
		<attribute id="122"/>
		<attribute id="123" name="3 Anschlüsse"/>
		<attribute id="124"/>
		<attribute id="125"/>
		<attribute id="126"/>
		<attribute id="127"/>
		<attribute id="128" name="&lt;200 Watt"/>
		<attribute id="129" name="&lt;250 Watt"/>
		<attribute id="130" name="&lt;300 Watt"/>
		<attribute id="131" name="&gt;=300 Watt"/>
		<attribute id="132" name="ISA Bus"/>
		<attribute id="133" name="EISA Bus"/>
		<attribute id="134" name="Local Bus"/>
		<attribute id="135" name="Motherboard"/>
		<attribute id="136" name="MCA"/>
		<attribute id="137" name="VESA Local Bus"/>
		<attribute id="138" name="Proprietary Local Bus"/>
		<attribute id="139" name="UBSA Local BUS"/>
		<attribute id="140" name="Ein paraller Port"/>
		<attribute id="141" name="Ein serieller Port"/>
		<attribute id="142" name="ein Mouse-Port"/>
		<attribute id="143" name="Zwei serielle Ports"/>
		<attribute id="144" name="Zwei parallele Ports"/>
		<attribute id="145" name="kein 5¼&quot; Schacht"/>
		<attribute id="146" name="kein 3½&quot; Schacht"/>
		<attribute id="147" name="ein 5½&quot; Schacht"/>
		<attribute id="148" name="zwei 5½&quot; Schächte"/>
		<attribute id="149" name="drei 5½&quot; Schächte"/>
		<attribute id="150" name="ein 3¼&quot; Schacht"/>
		<attribute id="151" name="zwei 3¼&quot; Schächte"/>
		<attribute id="152" name="drei 3¼&quot; Schächte"/>
		<attribute id="153" name="vier 5½&quot; Schächte"/>
		<attribute id="154" name="vier 3¼&quot; Schächte"/>
	</context>
	<diagram title="Preise für 486/66 PCs">
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
	<diagram title="Festplattengröße von 486/66 PCs">
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
	<diagram title="WinMarks (Graphics/Disk) für 486/66 PCs">
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
	<diagram title="Bustypen der 486/66 PCs">
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
	<diagram title="Festplattengrößen (ordinal)">
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
	<diagram title="Video (1000 Operationen je Sekunde)">
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
	<diagram title="Vertriebsform">
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
	<diagram title="Gehäusetyp">
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
	<diagram title="Zugängliche Laufwerke">
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
	<diagram title="Leistung des Netzteils und Zahl der Anschlüsse">
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
	<diagram title="Graphikkarte">
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
	<diagram title="Ports auf dem Motherboard">
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
	<diagram title="interne Laufwerksschächte">
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
		<edge from="7" to="8"/>
		<edge from="7" to="11"/>
		<edge from="8" to="12"/>
		<edge from="8" to="18"/>
		<edge from="9" to="10"/>
		<edge from="9" to="13"/>
		<edge from="10" to="11"/>
		<edge from="10" to="14"/>
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
