<?xml version="1.0" encoding="ISO-8859-1"?>
<conceptualSchema version="1.0" askDatabase="true" xmlns:xsi="http://www.w3.org/2000/10/XMLSchema-instance" xsi:noNamespaceSchemaLocation="http://meganesia.int.gu.edu.au/projects/ToscanaJ/schemas/csx.xsd">
        <database>
                <dsn>PCTEST80</dsn>
                <table>PC-Test</table>
                <key>PCBezeichnung</key>
        </database>
        <context>
                <object id="2">Bustyp='ISA'</object>
                <object id="3">Bustyp='EISA'</object>
                <object id="4">Bustyp='MCA'</object>
                <object id="5">(DISKmark&gt;0 and DISKmark&lt;=15 AND Graphics&gt;0 and Graphics&lt;=5 ) OR ( DISKmark&gt;0 and DISKmark&lt;=15 AND DOSmark&gt;=0 and DOSmark&lt;40)</object>
                <object id="6">(DISKmark&gt;0 and DISKmark&lt;=15 AND Graphics&gt;5 and Graphics&lt;=10) OR (DISKmark&gt;0 and DISKmark&lt;=15 AND DOSmark&gt;=40 and DOSmark&lt;50)</object>
                <object id="7">(DISKmark&gt;0 and DISKmark&lt;=15 AND Graphics&gt;10 and Graphics&lt;=15) OR (DISKmark&gt;0 and DISKmark&lt;=15 AND DOSmark&gt;=50 and DOSmark&lt;60)</object>
                <object id="8">(DISKmark&gt;0 and DISKmark&lt;=15 AND Graphics&gt;15 and Graphics&lt;=20) OR (DISKmark&gt;0 and DISKmark&lt;=15 AND DOSmark&gt;=60 and DOSmark&lt;70)</object>
                <object id="9">(DISKmark&gt;0 and DISKmark&lt;=15 AND Graphics&gt;20 and Graphics&lt;=30) OR (DISKmark&gt;0 and DISKmark&lt;=15 AND DOSmark&gt;=70 and DOSmark&lt;80)</object>
                <object id="10">(DISKmark&gt;0 and DISKmark&lt;=15 AND Graphics&gt;30 and Graphics&lt;=40) OR (DISKmark&gt;0 and DISKmark&lt;=15 AND DOSmark&gt;=80 and DOSmark&lt;90)</object>
                <object id="11">(DISKmark&gt;0 and DISKmark&lt;=15 AND Graphics&gt;40) OR (DISKmark&gt;0 and DISKmark&lt;=15 AND DOSmark&gt;=90)</object>
                <object id="12">(DISKmark&gt;15 and DISKmark&lt;=30 AND Graphics&gt;0 and Graphics&lt;=5) OR (DISKmark&gt;15 and DISKmark&lt;=30 AND DOSmark&gt;=0 and DOSmark&lt;40)</object>
                <object id="13">(DISKmark&gt;30 and DISKmark&lt;=45 AND Graphics&gt;0 and Graphics&lt;=5) OR (DISKmark&gt;30 and DISKmark&lt;=45 AND DOSmark&gt;=0 and DOSmark&lt;40)</object>
                <object id="14">(DISKmark&gt;45 and DISKmark&lt;=60 AND Graphics&gt;0 and Graphics&lt;=5) OR (DISKmark&gt;45 and DISKmark&lt;=60 AND DOSmark&gt;=0 and DOSmark&lt;40)</object>
                <object id="15">(DISKmark&gt;60 and DISKmark&lt;=90 AND Graphics&gt;0 and Graphics&lt;=5) OR (DISKmark&gt;60 and DISKmark&lt;=90 AND DOSmark&gt;=0 and DOSmark&lt;40)</object>
                <object id="16">(DISKmark&gt;90 and DISKmark&lt;=120 AND Graphics&gt;0 and Graphics&lt;=5) OR (DISKmark&gt;90 and DISKmark&lt;=120 AND DOSmark&gt;=0 and DOSmark&lt;40)</object>
                <object id="17">(DISKmark&gt;120 AND Graphics&gt;0 and Graphics&lt;=5) OR (DISKmark&gt;120 AND DOSmark&gt;=0 and DOSmark&lt;40)</object>
                <object id="18">(DISKmark&gt;15 and DISKmark&lt;=30 AND Graphics&gt;5 and Graphics&lt;=10) OR (DISKmark&gt;15 and DISKmark&lt;=30 AND DOSmark&gt;=40 and DOSmark&lt;50)</object>
                <object id="19">(DISKmark&gt;15 and DISKmark&lt;=30 AND Graphics&gt;10 and Graphics&lt;=15) OR (DISKmark&gt;15 and DISKmark&lt;=30 AND DOSmark&gt;=50 and DOSmark&lt;60)</object>
                <object id="20">(DISKmark&gt;15 and DISKmark&lt;=30 AND Graphics&gt;15 and Graphics&lt;=20) OR (DISKmark&gt;15 and DISKmark&lt;=30 AND DOSmark&gt;=60 and DOSmark&lt;70)</object>
                <object id="21">(DISKmark&gt;15 and DISKmark&lt;=30 AND Graphics&gt;20 and Graphics&lt;=30) OR (DISKmark&gt;15 and DISKmark&lt;=30 AND DOSmark&gt;=70 and DOSmark&lt;80)</object>
                <object id="22">(DISKmark&gt;15 and DISKmark&lt;=30 AND Graphics&gt;30 and Graphics&lt;=40) OR (DISKmark&gt;15 and DISKmark&lt;=30 AND DOSmark&gt;=80 and DOSmark&lt;90)</object>
                <object id="23">(DISKmark&gt;15 and DISKmark&lt;=30 AND Graphics&gt;40) OR (DISKmark&gt;15 and DISKmark&lt;=30 AND DOSmark&gt;=90)</object>
                <object id="24">(DISKmark&gt;30 and DISKmark&lt;=45 AND Graphics&gt;5 and Graphics&lt;=10) OR (DISKmark&gt;30 and DISKmark&lt;=45 AND DOSmark&gt;=40 and DOSmark&lt;50)</object>
                <object id="25">(DISKmark&gt;45 and DISKmark&lt;=60 AND Graphics&gt;5 and Graphics&lt;=10) OR (DISKmark&gt;45 and DISKmark&lt;=60 AND DOSmark&gt;=40 and DOSmark&lt;50)</object>
                <object id="26">(DISKmark&gt;60 and DISKmark&lt;=90 AND Graphics&gt;5 and Graphics&lt;=10) OR (DISKmark&gt;60 and DISKmark&lt;=90 AND DOSmark&gt;=40 and DOSmark&lt;50)</object>
                <object id="27">(DISKmark&gt;90 and DISKmark&lt;=120 AND Graphics&gt;5 and Graphics&lt;=10) OR (DISKmark&gt;90 and DISKmark&lt;=120 AND DOSmark&gt;=40 and DOSmark&lt;50)</object>
                <object id="28">(DISKmark&gt;120 AND Graphics&gt;5 and Graphics&lt;=10) OR (DISKmark&gt;120 AND DOSmark&gt;=40 and DOSmark&lt;50)</object>
                <object id="29">(DISKmark&gt;30 and DISKmark&lt;=45 AND Graphics&gt;10 and Graphics&lt;=15) OR (DISKmark&gt;30 and DISKmark&lt;=45 AND DOSmark&gt;=50 and DOSmark&lt;60)</object>
                <object id="30">(DISKmark&gt;30 and DISKmark&lt;=45 AND Graphics&gt;15 and Graphics&lt;=20) OR (DISKmark&gt;30 and DISKmark&lt;=45 AND DOSmark&gt;=60 and DOSmark&lt;70)</object>
                <object id="31">(DISKmark&gt;30 and DISKmark&lt;=45 AND Graphics&gt;20 and Graphics&lt;=30) OR (DISKmark&gt;30 and DISKmark&lt;=45 AND DOSmark&gt;=70 and DOSmark&lt;80)</object>
                <object id="32">(DISKmark&gt;30 and DISKmark&lt;=45 AND Graphics&gt;30 and Graphics&lt;=40) OR (DISKmark&gt;30 and DISKmark&lt;=45 AND DOSmark&gt;=80 and DOSmark&lt;90)</object>
                <object id="33">(DISKmark&gt;30 and DISKmark&lt;=45 AND Graphics&gt;40) OR (DISKmark&gt;30 and DISKmark&lt;=45 AND DOSmark&gt;=90)</object>
                <object id="34">(DISKmark&gt;45 and DISKmark&lt;=60 AND Graphics&gt;10 and Graphics&lt;=15) OR (DISKmark&gt;45 and DISKmark&lt;=60 AND DOSmark&gt;=50 and DOSmark&lt;60)</object>
                <object id="35">(DISKmark&gt;60 and DISKmark&lt;=90 AND Graphics&gt;10 and Graphics&lt;=15) OR (DISKmark&gt;60 and DISKmark&lt;=90 AND DOSmark&gt;=50 and DOSmark&lt;60)</object>
                <object id="36">(DISKmark&gt;90 and DISKmark&lt;=120 AND Graphics&gt;10 and Graphics&lt;=15) OR (DISKmark&gt;90 and DISKmark&lt;=120 AND DOSmark&gt;=50 and DOSmark&lt;60)</object>
                <object id="37">(DISKmark&gt;120 AND Graphics&gt;10 and Graphics&lt;=15) OR (DISKmark&gt;120 AND DOSmark&gt;=50 and DOSmark&lt;60)</object>
                <object id="38">(DISKmark&gt;45 and DISKmark&lt;=60 AND Graphics&gt;15 and Graphics&lt;=20) OR (DISKmark&gt;45 and DISKmark&lt;=60 AND DOSmark&gt;=60 and DOSmark&lt;70)</object>
                <object id="39">(DISKmark&gt;45 and DISKmark&lt;=60 AND Graphics&gt;20 and Graphics&lt;=30) OR (DISKmark&gt;45 and DISKmark&lt;=60 AND DOSmark&gt;=70 and DOSmark&lt;80)</object>
                <object id="40">(DISKmark&gt;45 and DISKmark&lt;=60 AND Graphics&gt;30 and Graphics&lt;=40) OR (DISKmark&gt;45 and DISKmark&lt;=60 AND DOSmark&gt;=80 and DOSmark&lt;90)</object>
                <object id="41">(DISKmark&gt;45 and DISKmark&lt;=60 AND Graphics&gt;40) OR (DISKmark&gt;45 and DISKmark&lt;=60 AND DOSmark&gt;=90)</object>
                <object id="42">(DISKmark&gt;60 and DISKmark&lt;=90 AND Graphics&gt;15 and Graphics&lt;=20) OR (DISKmark&gt;60 and DISKmark&lt;=90 AND DOSmark&gt;=60 and DOSmark&lt;70)</object>
                <object id="43">(DISKmark&gt;90 and DISKmark&lt;=120 AND Graphics&gt;15 and Graphics&lt;=20) OR (DISKmark&gt;90 and DISKmark&lt;=120 AND DOSmark&gt;=60 and DOSmark&lt;70)</object>
                <object id="44">(DISKmark&gt;120 AND Graphics&gt;15 and Graphics&lt;=20) OR (DISKmark&gt;120 AND DOSmark&gt;=60 and DOSmark&lt;70)</object>
                <object id="45">(DISKmark&gt;60 and DISKmark&lt;=90 AND Graphics&gt;20 and Graphics&lt;=30) OR (DISKmark&gt;60 and DISKmark&lt;=90 AND DOSmark&gt;=70 and DOSmark&lt;80)</object>
                <object id="46">(DISKmark&gt;60 and DISKmark&lt;=90 AND Graphics&gt;30 and Graphics&lt;=40) OR (DISKmark&gt;60 and DISKmark&lt;=90 AND DOSmark&gt;=80 and DOSmark&lt;90)</object>
                <object id="47">(DISKmark&gt;60 and DISKmark&lt;=90 AND Graphics&gt;40) OR (DISKmark&gt;60 and DISKmark&lt;=90 AND DOSmark&gt;=90)</object>
                <object id="48">(DISKmark&gt;90 and DISKmark&lt;=120 AND Graphics&gt;20 and Graphics&lt;=30) OR (DISKmark&gt;90 and DISKmark&lt;=120 AND DOSmark&gt;=70 and DOSmark&lt;80)</object>
                <object id="49">(DISKmark&gt;120 AND Graphics&gt;20 and Graphics&lt;=30) OR (DISKmark&gt;120 AND DOSmark&gt;=70 and DOSmark&lt;80)</object>
                <object id="50">(DISKmark&gt;90 and DISKmark&lt;=120 AND Graphics&gt;30 and Graphics&lt;=40) OR (DISKmark&gt;90 and DISKmark&lt;=120 AND DOSmark&gt;=80 and DOSmark&lt;90)</object>
                <object id="51">(DISKmark&gt;90 and DISKmark&lt;=120 AND Graphics&gt;40) OR (DISKmark&gt;90 and DISKmark&lt;=120 AND DOSmark&gt;=90)</object>
                <object id="52">(DISKmark&gt;120 AND Graphics&gt;30 and Graphics&lt;=40) OR (DISKmark&gt;120 AND DOSmark&gt;=80 and DOSmark&lt;90)</object>
                <object id="53">(DISKmark&gt;120 AND Graphics&gt;40) OR (DISKmark&gt;120 AND DOSmark&gt;=90)</object>
                <object id="54">Festplatte &lt; 200</object>
                <object id="55">Festplatte Between 200 And 399</object>
                <object id="56">Festplatte &gt;= 400</object>
                <object id="57">[Gehäusetyp]='Desktop'</object>
                <object id="58">[Gehäusetyp]='Slimline'</object>
                <object id="59">[Gehäusetyp]='Small-footprint'</object>
                <object id="60">[Gehäusetyp]='Tower'</object>
                <object id="61">[Gehäusetyp]='Minitower'</object>
                <object id="62">Graphikkarte='ISA'</object>
                <object id="63">Graphikkarte='EISA'</object>
                <object id="64">Graphikkarte='Local Bus'</object>
                <object id="65">Graphikkarte='Motherboard'</object>
                <object id="66">Graphikkarte='MCA'</object>
                <object id="67">Graphikkarte='VESA Local Bus'</object>
                <object id="68">Graphikkarte='Proprietary Local Bus'</object>
                <object id="69">Graphikkarte='UBSA Local Bus'</object>
                <object id="70">(Preis&gt;=5000) OR (Festplatte&gt;=500) OR (Video&gt;=9000)</object>
                <object id="71">((Preis&lt;5000 and Preis&gt;=4500)) OR ((Festplatte&lt;500 and Festplatte&gt;=450)) OR ((Video between 7500 and 8999))</object>
                <object id="72">((Preis&lt;4500 and Preis&gt;=4000)) OR ((Festplatte&lt;450 and Festplatte&gt;=400)) OR ((Video between 6000 and 7499))</object>
                <object id="73">((Preis&lt;4000 and Preis&gt;=3500)) OR ((Festplatte&lt;400 and Festplatte&gt;=350)) OR ((Video between 4500 and 5999))</object>
                <object id="74">((Preis&lt;3500 and Preis&gt;=3000)) OR ((Festplatte&lt;350 and Festplatte&gt;=300)) OR ((Video between 3000 and 4499))</object>
                <object id="75">((Preis&lt;3000 and Preis&gt;=2500)) OR ((Festplatte&lt;250 and Festplatte&gt;=225)) OR ((Video between 1500 and 2999))</object>
                <object id="76">((Preis&lt;2500) OR (Festplatte&lt;225) OR (Video&lt;1500))</object>
                <object id="77">[Freie Laufwerksschächte]=00</object>
                <object id="78">[Freie Laufwerksschächte]=01</object>
                <object id="79">[Freie Laufwerksschächte]=02</object>
                <object id="80">[Freie Laufwerksschächte]=03</object>
                <object id="81">[Freie Laufwerksschächte]=10</object>
                <object id="82">[Freie Laufwerksschächte]=11</object>
                <object id="83">[Freie Laufwerksschächte]=12</object>
                <object id="84">[Freie Laufwerksschächte]=13</object>
                <object id="85">[Freie Laufwerksschächte]=20</object>
                <object id="86">[Freie Laufwerksschächte]=21</object>
                <object id="87">[Freie Laufwerksschächte]=22</object>
                <object id="88">[Freie Laufwerksschächte]=23</object>
                <object id="89">[Freie Laufwerksschächte]=30</object>
                <object id="90">[Freie Laufwerksschächte]=31</object>
                <object id="91">[Freie Laufwerksschächte]=32</object>
                <object id="92">[Freie Laufwerksschächte]=33</object>
                <object id="93">[Freie Laufwerksschächte]=40</object>
                <object id="94">[Freie Laufwerksschächte]=41</object>
                <object id="95">[Freie Laufwerksschächte]=42</object>
                <object id="96">[Freie Laufwerksschächte]=43</object>
                <object id="97">[Freie Laufwerksschächte]=50</object>
                <object id="98">[Freie Laufwerksschächte]=51</object>
                <object id="99">[Freie Laufwerksschächte]=52</object>
                <object id="100">[Freie Laufwerksschächte]=53</object>
                <object id="101">[Freie Laufwerksschächte]=60</object>
                <object id="102">[Freie Laufwerksschächte]=61</object>
                <object id="103">[Freie Laufwerksschächte]=62</object>
                <object id="104">[Freie Laufwerksschächte]=63</object>
                <object id="105">[Freie Laufwerksschächte]=70</object>
                <object id="106">[Freie Laufwerksschächte]=71</object>
                <object id="107">[Freie Laufwerksschächte]=72</object>
                <object id="108">[Freie Laufwerksschächte]=73</object>
                <object id="109">[interne Laufwerkschächte]=00</object>
                <object id="110">[interne Laufwerkschächte]=10</object>
                <object id="111">[interne Laufwerkschächte]=20</object>
                <object id="112">[interne Laufwerkschächte]=30</object>
                <object id="113">[interne Laufwerkschächte]=01</object>
                <object id="114">[interne Laufwerkschächte]=11</object>
                <object id="115">[interne Laufwerkschächte]=21</object>
                <object id="116">[interne Laufwerkschächte]=31</object>
                <object id="117">[interne Laufwerkschächte]=02</object>
                <object id="118">[interne Laufwerkschächte]=12</object>
                <object id="119">[interne Laufwerkschächte]=22</object>
                <object id="120">[interne Laufwerkschächte]=32</object>
                <object id="121">[interne Laufwerkschächte]=03</object>
                <object id="122">[interne Laufwerkschächte]=13</object>
                <object id="123">[interne Laufwerkschächte]=23</object>
                <object id="124">[interne Laufwerkschächte]=33</object>
                <object id="125">[interne Laufwerkschächte]=40</object>
                <object id="126">[interne Laufwerkschächte]=41</object>
                <object id="127">[interne Laufwerkschächte]=42</object>
                <object id="128">[interne Laufwerkschächte]=43</object>
                <object id="129">[interne Laufwerkschächte]=04</object>
                <object id="130">[interne Laufwerkschächte]=14</object>
                <object id="131">[interne Laufwerkschächte]=24</object>
                <object id="132">[interne Laufwerkschächte]=34</object>
                <object id="133">[interne Laufwerkschächte]=44</object>
                <object id="134">Netzteil&lt;150 and [Netzteilanschlüsse]=5</object>
                <object id="135">Netzteil between 150 and 199 and [Netzteilanschlüsse]=5</object>
                <object id="136">Netzteil between 200 and 249 and [Netzteilanschlüsse]=5</object>
                <object id="137">Netzteil between 250 and 299 and [Netzteilanschlüsse]=5</object>
                <object id="138">Netzteil&gt;=300 and [Netzteilanschlüsse]=5</object>
                <object id="139">Netzteil&lt;150 and [Netzteilanschlüsse]=2</object>
                <object id="140">Netzteil&lt;150 and [Netzteilanschlüsse]=6</object>
                <object id="141">Netzteil between 150 and 199 and [Netzteilanschlüsse]=6</object>
                <object id="142">Netzteil between 200 and 249 and [Netzteilanschlüsse]=6</object>
                <object id="143">Netzteil between 250 and 299 and [Netzteilanschlüsse]=6</object>
                <object id="144">Netzteil&gt;=300 and [Netzteilanschlüsse]=6</object>
                <object id="145">Netzteil&lt;150 and [Netzteilanschlüsse]=7</object>
                <object id="146">Netzteil between 150 and 199 and [Netzteilanschlüsse]=7</object>
                <object id="147">Netzteil between 200 and 249 and [Netzteilanschlüsse]=7</object>
                <object id="148">Netzteil between 250 and 299 and [Netzteilanschlüsse]=7</object>
                <object id="149">Netzteil&gt;=300 and [Netzteilanschlüsse]=7</object>
                <object id="150">Netzteil&lt;150 and [Netzteilanschlüsse]=8</object>
                <object id="151">Netzteil between 150 and 199 and [Netzteilanschlüsse]=8</object>
                <object id="152">Netzteil between 200 and 249 and [Netzteilanschlüsse]=8</object>
                <object id="153">Netzteil between 250 and 299 and [Netzteilanschlüsse]=8</object>
                <object id="154">Netzteil&gt;=300 and [Netzteilanschlüsse]=8</object>
                <object id="155">Netzteil&lt;150 and [Netzteilanschlüsse]=4</object>
                <object id="156">Netzteil between 150 and 199 and [Netzteilanschlüsse]=4</object>
                <object id="157">Netzteil between 200 and 249 and [Netzteilanschlüsse]=4</object>
                <object id="158">Netzteil between 250 and 299 and [Netzteilanschlüsse]=4</object>
                <object id="159">Netzteil&gt;=300 and [Netzteilanschlüsse]=4</object>
                <object id="160">Netzteil&lt;150 and [Netzteilanschlüsse]=3</object>
                <object id="161">Netzteil between 150 and 199 and [Netzteilanschlüsse]=3</object>
                <object id="162">Netzteil between 200 and 249 and [Netzteilanschlüsse]=3</object>
                <object id="163">Netzteil between 250 and 299 and [Netzteilanschlüsse]=3</object>
                <object id="164">Netzteil&gt;=300 and [Netzteilanschlüsse]=3</object>
                <object id="165">Netzteil between 150 and 199 and [Netzteilanschlüsse]=2</object>
                <object id="166">Netzteil between 200 and 249 and [Netzteilanschlüsse]=2</object>
                <object id="167">Netzteil between 250 and 299 and [Netzteilanschlüsse]=2</object>
                <object id="168">Netzteil&gt;=300 and [Netzteilanschlüsse]=2</object>
                <object id="169">Ports='Keine'</object>
                <object id="170">Ports='1,1,0'</object>
                <object id="171">Ports='1,1,1'</object>
                <object id="172">Ports='1,2,0'</object>
                <object id="173">Ports='1,2,1'</object>
                <object id="174">Ports='2,1,0'</object>
                <object id="175">Ports='2,1,1'</object>
                <object id="176">Direktvertrieb=Yes and [Händler]=No</object>
                <object id="177">Direktvertrieb=No and [Händler]=Yes</object>
                <object id="178">Direktvertrieb=Yes and [Händler]=Yes</object>
                <attribute id="1">ISA-Bus</attribute>
                <attribute id="2">EISA-Bus</attribute>
                <attribute id="3">MCA-Bus</attribute>
                <attribute id="4">(Disk WinMark &gt; 0, Graphics WinMark &gt; 0) OR (Disk WinMark &gt; 0, DOSmark &gt;= 0)</attribute>
                <attribute id="5">(&gt; 5) OR (&gt;= 40)</attribute>
                <attribute id="6">(&gt; 10) OR (&gt;= 50)</attribute>
                <attribute id="7">(&gt; 15) OR (&gt;= 60)</attribute>
                <attribute id="8">(&gt; 20) OR (&gt;= 70)</attribute>
                <attribute id="9">(&gt; 30) OR (&gt;= 80)</attribute>
                <attribute id="10">(&gt; 40) OR (&gt;= 90)</attribute>
                <attribute id="11">(&gt; 15) OR (&gt; 15)</attribute>
                <attribute id="12">(&gt; 30) OR (&gt; 30)</attribute>
                <attribute id="13">(&gt; 45) OR (&gt; 45)</attribute>
                <attribute id="14">(&gt; 60) OR (&gt; 60)</attribute>
                <attribute id="15">(&gt; 90) OR (&gt; 90)</attribute>
                <attribute id="16">(&gt; 120) OR (&gt; 120)</attribute>
                <attribute id="17">&gt;=200MB</attribute>
                <attribute id="18">&gt;=400MB</attribute>
                <attribute id="19">Desktop</attribute>
                <attribute id="20">Slimline</attribute>
                <attribute id="21">Small-footprint</attribute>
                <attribute id="22">Tower</attribute>
                <attribute id="23">Mini-Tower</attribute>
                <attribute id="24">ISA Bus</attribute>
                <attribute id="25">EISA Bus</attribute>
                <attribute id="26">Local Bus</attribute>
                <attribute id="27">Motherboard</attribute>
                <attribute id="28">MCA</attribute>
                <attribute id="29">VESA Local Bus</attribute>
                <attribute id="30">Proprietary Local Bus</attribute>
                <attribute id="31">UBSA Local BUS</attribute>
                <attribute id="32">&lt;=7</attribute>
                <attribute id="33">&gt;=1</attribute>
                <attribute id="34">&gt;=2500$ OR &gt;=225MB OR &gt;=1500</attribute>
                <attribute id="35">&gt;=3000$ OR &gt;=250MB OR &gt;=3000</attribute>
                <attribute id="36">&gt;=3500$ OR &gt;=350MB OR &gt;=4500</attribute>
                <attribute id="37">&gt;=4000$ OR &gt;=400MB OR &gt;=6000</attribute>
                <attribute id="38">&gt;=4500$ OR &gt;=450MB OR &gt;=7500</attribute>
                <attribute id="39">&gt;=5000$ OR &gt;=500MB OR &gt;=9000</attribute>
                <attribute id="40">&lt;5000$ OR &lt;500MB OR &lt;9000</attribute>
                <attribute id="41">&lt;4500$ OR &lt;450MB OR &lt;7500</attribute>
                <attribute id="42">&lt;4000$ OR &lt;400MB OR &lt;6000</attribute>
                <attribute id="43">&lt;3500$ OR &lt;350MB OR &lt;4500</attribute>
                <attribute id="44">&lt;3000$ OR &lt;250MB OR &lt;3000</attribute>
                <attribute id="45">&lt;2500$ OR &lt;225MB OR &lt;1500</attribute>
                <attribute id="46">kein 5¼" Schacht</attribute>
                <attribute id="47">kein 3½" Schacht</attribute>
                <attribute id="48">ein 3½" Schacht</attribute>
                <attribute id="49">zwei 3½" Schächte</attribute>
                <attribute id="50">drei 3½" Schächte</attribute>
                <attribute id="51">ein 5¼" Schacht</attribute>
                <attribute id="52">zwei 5¼" Schächte</attribute>
                <attribute id="53">drei 5¼" Schächte</attribute>
                <attribute id="54">vier 5¼" Schächte</attribute>
                <attribute id="55">fünf 5¼" Schächte</attribute>
                <attribute id="56">sechs 5¼" Schächte</attribute>
                <attribute id="57">sieben 5¼" Schächte</attribute>
                <attribute id="58">kein 5¼" Schacht</attribute>
                <attribute id="59">kein 3½" Schacht</attribute>
                <attribute id="60">ein 5½" Schacht</attribute>
                <attribute id="61">zwei 5½" Schächte</attribute>
                <attribute id="62">drei 5½" Schächte</attribute>
                <attribute id="63">ein 3¼" Schacht</attribute>
                <attribute id="64">zwei 3¼" Schächte</attribute>
                <attribute id="65">drei 3¼" Schächte</attribute>
                <attribute id="66">vier 5½" Schächte</attribute>
                <attribute id="67">vier 3¼" Schächte</attribute>
                <attribute id="68">5 Anschlüsse</attribute>
                <attribute id="69">A2</attribute>
                <attribute id="70">A3</attribute>
                <attribute id="71">A4</attribute>
                <attribute id="72">A5</attribute>
                <attribute id="73">Leistung &lt;150 Watt</attribute>
                <attribute id="74">2 Anschlüsse</attribute>
                <attribute id="75">6 Anschlüsse</attribute>
                <attribute id="76">A10</attribute>
                <attribute id="77">A9</attribute>
                <attribute id="78">A8</attribute>
                <attribute id="79">A7</attribute>
                <attribute id="80">7 Anschlüsse</attribute>
                <attribute id="81">A13</attribute>
                <attribute id="82">A14</attribute>
                <attribute id="83">A15</attribute>
                <attribute id="84">A16</attribute>
                <attribute id="85">8 Anschlüsse</attribute>
                <attribute id="86">A20</attribute>
                <attribute id="87">A19</attribute>
                <attribute id="88">A18</attribute>
                <attribute id="89">A17</attribute>
                <attribute id="90">4 Anschlüsse</attribute>
                <attribute id="91">A23</attribute>
                <attribute id="92">A24</attribute>
                <attribute id="93">A25</attribute>
                <attribute id="94">A26</attribute>
                <attribute id="95">3 Anschlüsse</attribute>
                <attribute id="96">A30</attribute>
                <attribute id="97">A29</attribute>
                <attribute id="98">A28</attribute>
                <attribute id="99">A27</attribute>
                <attribute id="100">&lt;200 Watt</attribute>
                <attribute id="101">&lt;250 Watt</attribute>
                <attribute id="102">&lt;300 Watt</attribute>
                <attribute id="103">&gt;=300 Watt</attribute>
                <attribute id="104">Ein paraller Port</attribute>
                <attribute id="105">Ein serieller Port</attribute>
                <attribute id="106">ein Mouse-Port</attribute>
                <attribute id="107">Zwei serielle Ports</attribute>
                <attribute id="108">Zwei parallele Ports</attribute>
                <attribute id="109">Direktvertrieb</attribute>
                <attribute id="110">Nur Direktvertrieb</attribute>
                <attribute id="111">Händler</attribute>
                <attribute id="112">Nur Händlervertrieb</attribute>
                <attribute id="113">Beide Vertriebsformen</attribute>
        </context>
        <diagram title="bi12">
                <concept id="1">
                        <position x="0.000000" y="0.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-5.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                        </objectContingent>
                        <attributeContingent>
                        </attributeContingent>
                </concept>
                <concept id="2">
                        <position x="20.000000" y="-20.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="3.000000" y="-3.000000"/>
                                        <textAlignment>left</textAlignment>
                                </labelStyle>
                                <objectRef>2</objectRef>
                        </objectContingent>
                        <attributeContingent>
                                <labelStyle>
                                        <offset x="3.000000" y="3.000000"/>
                                        <textAlignment>left</textAlignment>
                                </labelStyle>
                                <attributeRef>1</attributeRef>
                        </attributeContingent>
                </concept>
                <concept id="3">
                        <position x="20.000000" y="-40.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="3.000000" y="-3.000000"/>
                                        <textAlignment>left</textAlignment>
                                </labelStyle>
                                <objectRef>3</objectRef>
                        </objectContingent>
                        <attributeContingent>
                                <labelStyle>
                                        <offset x="3.000000" y="3.000000"/>
                                        <textAlignment>left</textAlignment>
                                </labelStyle>
                                <attributeRef>2</attributeRef>
                        </attributeContingent>
                </concept>
                <concept id="4">
                        <position x="-20.000000" y="-30.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="-3.000000" y="-3.000000"/>
                                        <textAlignment>right</textAlignment>
                                </labelStyle>
                                <objectRef>4</objectRef>
                        </objectContingent>
                        <attributeContingent>
                                <labelStyle>
                                        <offset x="-3.000000" y="3.000000"/>
                                        <textAlignment>right</textAlignment>
                                </labelStyle>
                                <attributeRef>3</attributeRef>
                        </attributeContingent>
                </concept>
                <concept id="5">
                        <position x="0.000000" y="-60.000000"/>
                        <objectContingent>
                        </objectContingent>
                        <attributeContingent>
                        </attributeContingent>
                </concept>
                <edge from="1" to="2"/>
                <edge from="1" to="4"/>
                <edge from="2" to="3"/>
                <edge from="3" to="5"/>
                <edge from="4" to="5"/>
        </diagram>
        <diagram title="c7c7">
                <concept id="1">
                        <position x="0.000000" y="0.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-5.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>5</objectRef>
                        </objectContingent>
                        <attributeContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="5.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <attributeRef>4</attributeRef>
                        </attributeContingent>
                </concept>
                <concept id="2">
                        <position x="10.000000" y="-10.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-5.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>6</objectRef>
                        </objectContingent>
                        <attributeContingent>
                                <labelStyle>
                                        <offset x="5.000000" y="5.000000"/>
                                        <textAlignment>left</textAlignment>
                                </labelStyle>
                                <attributeRef>5</attributeRef>
                        </attributeContingent>
                </concept>
                <concept id="3">
                        <position x="20.000000" y="-20.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-5.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>7</objectRef>
                        </objectContingent>
                        <attributeContingent>
                                <labelStyle>
                                        <offset x="5.000000" y="5.000000"/>
                                        <textAlignment>left</textAlignment>
                                </labelStyle>
                                <attributeRef>6</attributeRef>
                        </attributeContingent>
                </concept>
                <concept id="4">
                        <position x="30.000000" y="-30.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-5.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>8</objectRef>
                        </objectContingent>
                        <attributeContingent>
                                <labelStyle>
                                        <offset x="5.000000" y="5.000000"/>
                                        <textAlignment>left</textAlignment>
                                </labelStyle>
                                <attributeRef>7</attributeRef>
                        </attributeContingent>
                </concept>
                <concept id="5">
                        <position x="40.000000" y="-40.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-5.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>9</objectRef>
                        </objectContingent>
                        <attributeContingent>
                                <labelStyle>
                                        <offset x="5.000000" y="5.000000"/>
                                        <textAlignment>left</textAlignment>
                                </labelStyle>
                                <attributeRef>8</attributeRef>
                        </attributeContingent>
                </concept>
                <concept id="6">
                        <position x="50.000000" y="-50.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-5.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>10</objectRef>
                        </objectContingent>
                        <attributeContingent>
                                <labelStyle>
                                        <offset x="5.000000" y="5.000000"/>
                                        <textAlignment>left</textAlignment>
                                </labelStyle>
                                <attributeRef>9</attributeRef>
                        </attributeContingent>
                </concept>
                <concept id="7">
                        <position x="60.000000" y="-60.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-5.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>11</objectRef>
                        </objectContingent>
                        <attributeContingent>
                                <labelStyle>
                                        <offset x="5.000000" y="5.000000"/>
                                        <textAlignment>left</textAlignment>
                                </labelStyle>
                                <attributeRef>10</attributeRef>
                        </attributeContingent>
                </concept>
                <concept id="8">
                        <position x="-10.000000" y="-10.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-5.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>12</objectRef>
                        </objectContingent>
                        <attributeContingent>
                                <labelStyle>
                                        <offset x="-5.000000" y="5.000000"/>
                                        <textAlignment>right</textAlignment>
                                </labelStyle>
                                <attributeRef>11</attributeRef>
                        </attributeContingent>
                </concept>
                <concept id="9">
                        <position x="-20.000000" y="-20.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-5.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>13</objectRef>
                        </objectContingent>
                        <attributeContingent>
                                <labelStyle>
                                        <offset x="-5.000000" y="5.000000"/>
                                        <textAlignment>right</textAlignment>
                                </labelStyle>
                                <attributeRef>12</attributeRef>
                        </attributeContingent>
                </concept>
                <concept id="10">
                        <position x="-30.000000" y="-30.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-5.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>14</objectRef>
                        </objectContingent>
                        <attributeContingent>
                                <labelStyle>
                                        <offset x="-5.000000" y="5.000000"/>
                                        <textAlignment>right</textAlignment>
                                </labelStyle>
                                <attributeRef>13</attributeRef>
                        </attributeContingent>
                </concept>
                <concept id="11">
                        <position x="-40.000000" y="-40.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-5.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>15</objectRef>
                        </objectContingent>
                        <attributeContingent>
                                <labelStyle>
                                        <offset x="-5.000000" y="5.000000"/>
                                        <textAlignment>right</textAlignment>
                                </labelStyle>
                                <attributeRef>14</attributeRef>
                        </attributeContingent>
                </concept>
                <concept id="12">
                        <position x="-50.000000" y="-50.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-5.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>16</objectRef>
                        </objectContingent>
                        <attributeContingent>
                                <labelStyle>
                                        <offset x="-5.000000" y="5.000000"/>
                                        <textAlignment>right</textAlignment>
                                </labelStyle>
                                <attributeRef>15</attributeRef>
                        </attributeContingent>
                </concept>
                <concept id="13">
                        <position x="-60.000000" y="-60.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-5.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>17</objectRef>
                        </objectContingent>
                        <attributeContingent>
                                <labelStyle>
                                        <offset x="-5.000000" y="5.000000"/>
                                        <textAlignment>right</textAlignment>
                                </labelStyle>
                                <attributeRef>16</attributeRef>
                        </attributeContingent>
                </concept>
                <concept id="14">
                        <position x="0.000000" y="-20.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-5.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>18</objectRef>
                        </objectContingent>
                        <attributeContingent>
                        </attributeContingent>
                </concept>
                <concept id="15">
                        <position x="10.000000" y="-30.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-5.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>19</objectRef>
                        </objectContingent>
                        <attributeContingent>
                        </attributeContingent>
                </concept>
                <concept id="16">
                        <position x="20.000000" y="-40.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-5.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>20</objectRef>
                        </objectContingent>
                        <attributeContingent>
                        </attributeContingent>
                </concept>
                <concept id="17">
                        <position x="30.000000" y="-50.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-5.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>21</objectRef>
                        </objectContingent>
                        <attributeContingent>
                        </attributeContingent>
                </concept>
                <concept id="18">
                        <position x="40.000000" y="-60.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-5.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>22</objectRef>
                        </objectContingent>
                        <attributeContingent>
                        </attributeContingent>
                </concept>
                <concept id="19">
                        <position x="50.000000" y="-70.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-5.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>23</objectRef>
                        </objectContingent>
                        <attributeContingent>
                        </attributeContingent>
                </concept>
                <concept id="20">
                        <position x="-10.000000" y="-30.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-5.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>24</objectRef>
                        </objectContingent>
                        <attributeContingent>
                        </attributeContingent>
                </concept>
                <concept id="21">
                        <position x="-20.000000" y="-40.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-5.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>25</objectRef>
                        </objectContingent>
                        <attributeContingent>
                        </attributeContingent>
                </concept>
                <concept id="22">
                        <position x="-30.000000" y="-50.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-5.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>26</objectRef>
                        </objectContingent>
                        <attributeContingent>
                        </attributeContingent>
                </concept>
                <concept id="23">
                        <position x="-40.000000" y="-60.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-5.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>27</objectRef>
                        </objectContingent>
                        <attributeContingent>
                        </attributeContingent>
                </concept>
                <concept id="24">
                        <position x="-50.000000" y="-70.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-5.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>28</objectRef>
                        </objectContingent>
                        <attributeContingent>
                        </attributeContingent>
                </concept>
                <concept id="25">
                        <position x="0.000000" y="-40.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-5.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>29</objectRef>
                        </objectContingent>
                        <attributeContingent>
                        </attributeContingent>
                </concept>
                <concept id="26">
                        <position x="10.000000" y="-50.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-5.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>30</objectRef>
                        </objectContingent>
                        <attributeContingent>
                        </attributeContingent>
                </concept>
                <concept id="27">
                        <position x="20.000000" y="-60.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-5.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>31</objectRef>
                        </objectContingent>
                        <attributeContingent>
                        </attributeContingent>
                </concept>
                <concept id="28">
                        <position x="30.000000" y="-70.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-5.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>32</objectRef>
                        </objectContingent>
                        <attributeContingent>
                        </attributeContingent>
                </concept>
                <concept id="29">
                        <position x="40.000000" y="-80.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-5.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>33</objectRef>
                        </objectContingent>
                        <attributeContingent>
                        </attributeContingent>
                </concept>
                <concept id="30">
                        <position x="-10.000000" y="-50.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-5.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>34</objectRef>
                        </objectContingent>
                        <attributeContingent>
                        </attributeContingent>
                </concept>
                <concept id="31">
                        <position x="-20.000000" y="-60.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-5.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>35</objectRef>
                        </objectContingent>
                        <attributeContingent>
                        </attributeContingent>
                </concept>
                <concept id="32">
                        <position x="-30.000000" y="-70.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-5.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>36</objectRef>
                        </objectContingent>
                        <attributeContingent>
                        </attributeContingent>
                </concept>
                <concept id="33">
                        <position x="-40.000000" y="-80.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-5.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>37</objectRef>
                        </objectContingent>
                        <attributeContingent>
                        </attributeContingent>
                </concept>
                <concept id="34">
                        <position x="0.000000" y="-60.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-5.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>38</objectRef>
                        </objectContingent>
                        <attributeContingent>
                        </attributeContingent>
                </concept>
                <concept id="35">
                        <position x="10.000000" y="-70.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-5.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>39</objectRef>
                        </objectContingent>
                        <attributeContingent>
                        </attributeContingent>
                </concept>
                <concept id="36">
                        <position x="20.000000" y="-80.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-5.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>40</objectRef>
                        </objectContingent>
                        <attributeContingent>
                        </attributeContingent>
                </concept>
                <concept id="37">
                        <position x="30.000000" y="-90.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-5.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>41</objectRef>
                        </objectContingent>
                        <attributeContingent>
                        </attributeContingent>
                </concept>
                <concept id="38">
                        <position x="-10.000000" y="-70.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-5.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>42</objectRef>
                        </objectContingent>
                        <attributeContingent>
                        </attributeContingent>
                </concept>
                <concept id="39">
                        <position x="-20.000000" y="-80.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-5.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>43</objectRef>
                        </objectContingent>
                        <attributeContingent>
                        </attributeContingent>
                </concept>
                <concept id="40">
                        <position x="-30.000000" y="-90.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-5.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>44</objectRef>
                        </objectContingent>
                        <attributeContingent>
                        </attributeContingent>
                </concept>
                <concept id="41">
                        <position x="0.000000" y="-80.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-5.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>45</objectRef>
                        </objectContingent>
                        <attributeContingent>
                        </attributeContingent>
                </concept>
                <concept id="42">
                        <position x="10.000000" y="-90.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-5.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>46</objectRef>
                        </objectContingent>
                        <attributeContingent>
                        </attributeContingent>
                </concept>
                <concept id="43">
                        <position x="20.000000" y="-100.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-5.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>47</objectRef>
                        </objectContingent>
                        <attributeContingent>
                        </attributeContingent>
                </concept>
                <concept id="44">
                        <position x="-10.000000" y="-90.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-5.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>48</objectRef>
                        </objectContingent>
                        <attributeContingent>
                        </attributeContingent>
                </concept>
                <concept id="45">
                        <position x="-20.000000" y="-100.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-5.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>49</objectRef>
                        </objectContingent>
                        <attributeContingent>
                        </attributeContingent>
                </concept>
                <concept id="46">
                        <position x="0.000000" y="-100.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-5.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>50</objectRef>
                        </objectContingent>
                        <attributeContingent>
                        </attributeContingent>
                </concept>
                <concept id="47">
                        <position x="10.000000" y="-110.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-5.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>51</objectRef>
                        </objectContingent>
                        <attributeContingent>
                        </attributeContingent>
                </concept>
                <concept id="48">
                        <position x="-10.000000" y="-110.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-5.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>52</objectRef>
                        </objectContingent>
                        <attributeContingent>
                        </attributeContingent>
                </concept>
                <concept id="49">
                        <position x="0.000000" y="-120.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-5.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>53</objectRef>
                        </objectContingent>
                        <attributeContingent>
                        </attributeContingent>
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
        <diagram title="Ordinalskala O3">
                <concept id="1">
                        <position x="0.000000" y="0.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="3.000000" y="-3.000000"/>
                                        <textAlignment>left</textAlignment>
                                </labelStyle>
                                <objectRef>54</objectRef>
                        </objectContingent>
                        <attributeContingent>
                        </attributeContingent>
                </concept>
                <concept id="2">
                        <position x="0.000000" y="-20.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="3.000000" y="-3.000000"/>
                                        <textAlignment>left</textAlignment>
                                </labelStyle>
                                <objectRef>55</objectRef>
                        </objectContingent>
                        <attributeContingent>
                                <labelStyle>
                                        <offset x="3.000000" y="3.000000"/>
                                        <textAlignment>left</textAlignment>
                                </labelStyle>
                                <attributeRef>17</attributeRef>
                        </attributeContingent>
                </concept>
                <concept id="3">
                        <position x="0.000000" y="-40.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="3.000000" y="-3.000000"/>
                                        <textAlignment>left</textAlignment>
                                </labelStyle>
                                <objectRef>56</objectRef>
                        </objectContingent>
                        <attributeContingent>
                                <labelStyle>
                                        <offset x="3.000000" y="3.000000"/>
                                        <textAlignment>left</textAlignment>
                                </labelStyle>
                                <attributeRef>18</attributeRef>
                        </attributeContingent>
                </concept>
                <edge from="1" to="2"/>
                <edge from="2" to="3"/>
        </diagram>
        <diagram title="Gehäusetyp">
                <concept id="1">
                        <position x="0.000000" y="0.000000"/>
                        <objectContingent>
                        </objectContingent>
                        <attributeContingent>
                        </attributeContingent>
                </concept>
                <concept id="2">
                        <position x="20.000000" y="-20.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-3.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>57</objectRef>
                        </objectContingent>
                        <attributeContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="3.500000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <attributeRef>19</attributeRef>
                        </attributeContingent>
                </concept>
                <concept id="3">
                        <position x="40.000000" y="-40.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-3.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>58</objectRef>
                        </objectContingent>
                        <attributeContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="3.500000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <attributeRef>20</attributeRef>
                        </attributeContingent>
                </concept>
                <concept id="4">
                        <position x="0.000000" y="-40.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-3.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>59</objectRef>
                        </objectContingent>
                        <attributeContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="3.500000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <attributeRef>21</attributeRef>
                        </attributeContingent>
                </concept>
                <concept id="5">
                        <position x="-20.000000" y="-20.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-3.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>60</objectRef>
                        </objectContingent>
                        <attributeContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="3.500000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <attributeRef>22</attributeRef>
                        </attributeContingent>
                </concept>
                <concept id="6">
                        <position x="-40.000000" y="-40.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-3.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>61</objectRef>
                        </objectContingent>
                        <attributeContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="3.500000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <attributeRef>23</attributeRef>
                        </attributeContingent>
                </concept>
                <concept id="7">
                        <position x="0.000000" y="-70.000000"/>
                        <objectContingent>
                        </objectContingent>
                        <attributeContingent>
                        </attributeContingent>
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
        <diagram title="Graphikkarte">
                <concept id="1">
                        <position x="-20.000000" y="0.000000"/>
                        <objectContingent>
                        </objectContingent>
                        <attributeContingent>
                        </attributeContingent>
                </concept>
                <concept id="2">
                        <position x="-80.000000" y="-30.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-3.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>62</objectRef>
                        </objectContingent>
                        <attributeContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="2.500000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <attributeRef>24</attributeRef>
                        </attributeContingent>
                </concept>
                <concept id="3">
                        <position x="-80.000000" y="-50.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-3.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>63</objectRef>
                        </objectContingent>
                        <attributeContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="2.500000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <attributeRef>25</attributeRef>
                        </attributeContingent>
                </concept>
                <concept id="4">
                        <position x="-20.000000" y="-20.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-3.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>64</objectRef>
                        </objectContingent>
                        <attributeContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="2.500000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <attributeRef>26</attributeRef>
                        </attributeContingent>
                </concept>
                <concept id="5">
                        <position x="20.000000" y="-40.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-3.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>65</objectRef>
                        </objectContingent>
                        <attributeContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="2.500000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <attributeRef>27</attributeRef>
                        </attributeContingent>
                </concept>
                <concept id="6">
                        <position x="-60.000000" y="-40.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-3.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>66</objectRef>
                        </objectContingent>
                        <attributeContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="2.500000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <attributeRef>28</attributeRef>
                        </attributeContingent>
                </concept>
                <concept id="7">
                        <position x="0.000000" y="-40.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-3.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>67</objectRef>
                        </objectContingent>
                        <attributeContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="2.500000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <attributeRef>29</attributeRef>
                        </attributeContingent>
                </concept>
                <concept id="8">
                        <position x="-40.000000" y="-40.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-3.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>68</objectRef>
                        </objectContingent>
                        <attributeContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="2.500000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <attributeRef>30</attributeRef>
                        </attributeContingent>
                </concept>
                <concept id="9">
                        <position x="-20.000000" y="-40.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-3.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>69</objectRef>
                        </objectContingent>
                        <attributeContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="2.500000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <attributeRef>31</attributeRef>
                        </attributeContingent>
                </concept>
                <concept id="10">
                        <position x="-20.000000" y="-70.000000"/>
                        <objectContingent>
                        </objectContingent>
                        <attributeContingent>
                        </attributeContingent>
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
        <diagram title="I7">
                <concept id="1">
                        <position x="0.000000" y="0.000000"/>
                        <objectContingent>
                        </objectContingent>
                        <attributeContingent>
                                <labelStyle>
                                        <offset x="-3.000000" y="3.000000"/>
                                        <textAlignment>right</textAlignment>
                                </labelStyle>
                                <attributeRef>32</attributeRef>
                                <attributeRef>33</attributeRef>
                        </attributeContingent>
                </concept>
                <concept id="2">
                        <position x="20.000000" y="-20.000000"/>
                        <objectContingent>
                        </objectContingent>
                        <attributeContingent>
                                <labelStyle>
                                        <offset x="3.000000" y="3.000000"/>
                                        <textAlignment>left</textAlignment>
                                </labelStyle>
                                <attributeRef>34</attributeRef>
                        </attributeContingent>
                </concept>
                <concept id="3">
                        <position x="40.000000" y="-40.000000"/>
                        <objectContingent>
                        </objectContingent>
                        <attributeContingent>
                                <labelStyle>
                                        <offset x="3.000000" y="3.000000"/>
                                        <textAlignment>left</textAlignment>
                                </labelStyle>
                                <attributeRef>35</attributeRef>
                        </attributeContingent>
                </concept>
                <concept id="4">
                        <position x="60.000000" y="-60.000000"/>
                        <objectContingent>
                        </objectContingent>
                        <attributeContingent>
                                <labelStyle>
                                        <offset x="3.000000" y="3.000000"/>
                                        <textAlignment>left</textAlignment>
                                </labelStyle>
                                <attributeRef>36</attributeRef>
                        </attributeContingent>
                </concept>
                <concept id="5">
                        <position x="80.000000" y="-80.000000"/>
                        <objectContingent>
                        </objectContingent>
                        <attributeContingent>
                                <labelStyle>
                                        <offset x="3.000000" y="3.000000"/>
                                        <textAlignment>left</textAlignment>
                                </labelStyle>
                                <attributeRef>37</attributeRef>
                        </attributeContingent>
                </concept>
                <concept id="6">
                        <position x="100.000000" y="-100.000000"/>
                        <objectContingent>
                        </objectContingent>
                        <attributeContingent>
                                <labelStyle>
                                        <offset x="3.000000" y="3.000000"/>
                                        <textAlignment>left</textAlignment>
                                </labelStyle>
                                <attributeRef>38</attributeRef>
                        </attributeContingent>
                </concept>
                <concept id="7">
                        <position x="120.000000" y="-120.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-4.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>70</objectRef>
                        </objectContingent>
                        <attributeContingent>
                                <labelStyle>
                                        <offset x="3.000000" y="3.000000"/>
                                        <textAlignment>left</textAlignment>
                                </labelStyle>
                                <attributeRef>39</attributeRef>
                        </attributeContingent>
                </concept>
                <concept id="8">
                        <position x="-20.000000" y="-20.000000"/>
                        <objectContingent>
                        </objectContingent>
                        <attributeContingent>
                                <labelStyle>
                                        <offset x="-3.000000" y="3.000000"/>
                                        <textAlignment>right</textAlignment>
                                </labelStyle>
                                <attributeRef>40</attributeRef>
                        </attributeContingent>
                </concept>
                <concept id="9">
                        <position x="0.000000" y="-40.000000"/>
                        <objectContingent>
                        </objectContingent>
                        <attributeContingent>
                        </attributeContingent>
                </concept>
                <concept id="10">
                        <position x="20.000000" y="-60.000000"/>
                        <objectContingent>
                        </objectContingent>
                        <attributeContingent>
                        </attributeContingent>
                </concept>
                <concept id="11">
                        <position x="40.000000" y="-80.000000"/>
                        <objectContingent>
                        </objectContingent>
                        <attributeContingent>
                        </attributeContingent>
                </concept>
                <concept id="12">
                        <position x="60.000000" y="-100.000000"/>
                        <objectContingent>
                        </objectContingent>
                        <attributeContingent>
                        </attributeContingent>
                </concept>
                <concept id="13">
                        <position x="80.000000" y="-120.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-4.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>71</objectRef>
                        </objectContingent>
                        <attributeContingent>
                        </attributeContingent>
                </concept>
                <concept id="14">
                        <position x="-40.000000" y="-40.000000"/>
                        <objectContingent>
                        </objectContingent>
                        <attributeContingent>
                                <labelStyle>
                                        <offset x="-3.000000" y="3.000000"/>
                                        <textAlignment>right</textAlignment>
                                </labelStyle>
                                <attributeRef>41</attributeRef>
                        </attributeContingent>
                </concept>
                <concept id="15">
                        <position x="-20.000000" y="-60.000000"/>
                        <objectContingent>
                        </objectContingent>
                        <attributeContingent>
                        </attributeContingent>
                </concept>
                <concept id="16">
                        <position x="0.000000" y="-80.000000"/>
                        <objectContingent>
                        </objectContingent>
                        <attributeContingent>
                        </attributeContingent>
                </concept>
                <concept id="17">
                        <position x="20.000000" y="-100.000000"/>
                        <objectContingent>
                        </objectContingent>
                        <attributeContingent>
                        </attributeContingent>
                </concept>
                <concept id="18">
                        <position x="40.000000" y="-120.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-4.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>72</objectRef>
                        </objectContingent>
                        <attributeContingent>
                        </attributeContingent>
                </concept>
                <concept id="19">
                        <position x="-60.000000" y="-60.000000"/>
                        <objectContingent>
                        </objectContingent>
                        <attributeContingent>
                                <labelStyle>
                                        <offset x="-3.000000" y="3.000000"/>
                                        <textAlignment>right</textAlignment>
                                </labelStyle>
                                <attributeRef>42</attributeRef>
                        </attributeContingent>
                </concept>
                <concept id="20">
                        <position x="-40.000000" y="-80.000000"/>
                        <objectContingent>
                        </objectContingent>
                        <attributeContingent>
                        </attributeContingent>
                </concept>
                <concept id="21">
                        <position x="-20.000000" y="-100.000000"/>
                        <objectContingent>
                        </objectContingent>
                        <attributeContingent>
                        </attributeContingent>
                </concept>
                <concept id="22">
                        <position x="0.000000" y="-120.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-4.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>73</objectRef>
                        </objectContingent>
                        <attributeContingent>
                        </attributeContingent>
                </concept>
                <concept id="23">
                        <position x="-80.000000" y="-80.000000"/>
                        <objectContingent>
                        </objectContingent>
                        <attributeContingent>
                                <labelStyle>
                                        <offset x="-3.000000" y="3.000000"/>
                                        <textAlignment>right</textAlignment>
                                </labelStyle>
                                <attributeRef>43</attributeRef>
                        </attributeContingent>
                </concept>
                <concept id="24">
                        <position x="-60.000000" y="-100.000000"/>
                        <objectContingent>
                        </objectContingent>
                        <attributeContingent>
                        </attributeContingent>
                </concept>
                <concept id="25">
                        <position x="-40.000000" y="-120.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-4.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>74</objectRef>
                        </objectContingent>
                        <attributeContingent>
                        </attributeContingent>
                </concept>
                <concept id="26">
                        <position x="-100.000000" y="-100.000000"/>
                        <objectContingent>
                        </objectContingent>
                        <attributeContingent>
                                <labelStyle>
                                        <offset x="-3.000000" y="3.000000"/>
                                        <textAlignment>right</textAlignment>
                                </labelStyle>
                                <attributeRef>44</attributeRef>
                        </attributeContingent>
                </concept>
                <concept id="27">
                        <position x="-80.000000" y="-120.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-4.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>75</objectRef>
                        </objectContingent>
                        <attributeContingent>
                        </attributeContingent>
                </concept>
                <concept id="28">
                        <position x="-120.000000" y="-120.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-4.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>76</objectRef>
                        </objectContingent>
                        <attributeContingent>
                                <labelStyle>
                                        <offset x="-3.000000" y="3.000000"/>
                                        <textAlignment>right</textAlignment>
                                </labelStyle>
                                <attributeRef>45</attributeRef>
                        </attributeContingent>
                </concept>
                <concept id="29">
                        <position x="0.000000" y="-160.000000"/>
                        <objectContingent>
                        </objectContingent>
                        <attributeContingent>
                        </attributeContingent>
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
        <diagram title="Zugängliche Laufwerke">
                <concept id="1">
                        <position x="0.000000" y="0.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-3.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>77</objectRef>
                        </objectContingent>
                        <attributeContingent>
                                <labelStyle>
                                        <offset x="-3.500000" y="2.400000"/>
                                        <textAlignment>right</textAlignment>
                                </labelStyle>
                                <attributeRef>46</attributeRef>
                                <attributeRef>47</attributeRef>
                        </attributeContingent>
                </concept>
                <concept id="2">
                        <position x="10.000000" y="-10.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-3.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>78</objectRef>
                        </objectContingent>
                        <attributeContingent>
                                <labelStyle>
                                        <offset x="3.000000" y="3.000000"/>
                                        <textAlignment>left</textAlignment>
                                </labelStyle>
                                <attributeRef>48</attributeRef>
                        </attributeContingent>
                </concept>
                <concept id="3">
                        <position x="20.000000" y="-20.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-3.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>79</objectRef>
                        </objectContingent>
                        <attributeContingent>
                                <labelStyle>
                                        <offset x="3.000000" y="3.000000"/>
                                        <textAlignment>left</textAlignment>
                                </labelStyle>
                                <attributeRef>49</attributeRef>
                        </attributeContingent>
                </concept>
                <concept id="4">
                        <position x="30.000000" y="-30.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-3.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>80</objectRef>
                        </objectContingent>
                        <attributeContingent>
                                <labelStyle>
                                        <offset x="3.000000" y="3.000000"/>
                                        <textAlignment>left</textAlignment>
                                </labelStyle>
                                <attributeRef>50</attributeRef>
                        </attributeContingent>
                </concept>
                <concept id="5">
                        <position x="-10.000000" y="-10.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-3.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>81</objectRef>
                        </objectContingent>
                        <attributeContingent>
                                <labelStyle>
                                        <offset x="-0.900000" y="2.800000"/>
                                        <textAlignment>right</textAlignment>
                                </labelStyle>
                                <attributeRef>51</attributeRef>
                        </attributeContingent>
                </concept>
                <concept id="6">
                        <position x="0.000000" y="-20.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-3.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>82</objectRef>
                        </objectContingent>
                        <attributeContingent>
                        </attributeContingent>
                </concept>
                <concept id="7">
                        <position x="10.000000" y="-30.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-3.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>83</objectRef>
                        </objectContingent>
                        <attributeContingent>
                        </attributeContingent>
                </concept>
                <concept id="8">
                        <position x="20.000000" y="-40.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-3.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>84</objectRef>
                        </objectContingent>
                        <attributeContingent>
                        </attributeContingent>
                </concept>
                <concept id="9">
                        <position x="-20.000000" y="-20.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-3.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>85</objectRef>
                        </objectContingent>
                        <attributeContingent>
                                <labelStyle>
                                        <offset x="-1.100000" y="3.000000"/>
                                        <textAlignment>right</textAlignment>
                                </labelStyle>
                                <attributeRef>52</attributeRef>
                        </attributeContingent>
                </concept>
                <concept id="10">
                        <position x="-10.000000" y="-30.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-3.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>86</objectRef>
                        </objectContingent>
                        <attributeContingent>
                        </attributeContingent>
                </concept>
                <concept id="11">
                        <position x="0.000000" y="-40.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-3.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>87</objectRef>
                        </objectContingent>
                        <attributeContingent>
                        </attributeContingent>
                </concept>
                <concept id="12">
                        <position x="10.000000" y="-50.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-3.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>88</objectRef>
                        </objectContingent>
                        <attributeContingent>
                        </attributeContingent>
                </concept>
                <concept id="13">
                        <position x="-30.000000" y="-30.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-3.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>89</objectRef>
                        </objectContingent>
                        <attributeContingent>
                                <labelStyle>
                                        <offset x="-2.200000" y="2.800000"/>
                                        <textAlignment>right</textAlignment>
                                </labelStyle>
                                <attributeRef>53</attributeRef>
                        </attributeContingent>
                </concept>
                <concept id="14">
                        <position x="-20.000000" y="-40.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-3.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>90</objectRef>
                        </objectContingent>
                        <attributeContingent>
                        </attributeContingent>
                </concept>
                <concept id="15">
                        <position x="-10.000000" y="-50.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-3.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>91</objectRef>
                        </objectContingent>
                        <attributeContingent>
                        </attributeContingent>
                </concept>
                <concept id="16">
                        <position x="0.000000" y="-60.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-3.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>92</objectRef>
                        </objectContingent>
                        <attributeContingent>
                        </attributeContingent>
                </concept>
                <concept id="17">
                        <position x="-40.000000" y="-40.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-3.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>93</objectRef>
                        </objectContingent>
                        <attributeContingent>
                                <labelStyle>
                                        <offset x="-1.600000" y="3.200000"/>
                                        <textAlignment>right</textAlignment>
                                </labelStyle>
                                <attributeRef>54</attributeRef>
                        </attributeContingent>
                </concept>
                <concept id="18">
                        <position x="-30.000000" y="-50.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-3.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>94</objectRef>
                        </objectContingent>
                        <attributeContingent>
                        </attributeContingent>
                </concept>
                <concept id="19">
                        <position x="-20.000000" y="-60.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-3.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>95</objectRef>
                        </objectContingent>
                        <attributeContingent>
                        </attributeContingent>
                </concept>
                <concept id="20">
                        <position x="-10.000000" y="-70.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-3.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>96</objectRef>
                        </objectContingent>
                        <attributeContingent>
                        </attributeContingent>
                </concept>
                <concept id="21">
                        <position x="-50.000000" y="-50.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-3.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>97</objectRef>
                        </objectContingent>
                        <attributeContingent>
                                <labelStyle>
                                        <offset x="-1.500000" y="2.400000"/>
                                        <textAlignment>right</textAlignment>
                                </labelStyle>
                                <attributeRef>55</attributeRef>
                        </attributeContingent>
                </concept>
                <concept id="22">
                        <position x="-40.000000" y="-60.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-3.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>98</objectRef>
                        </objectContingent>
                        <attributeContingent>
                        </attributeContingent>
                </concept>
                <concept id="23">
                        <position x="-30.000000" y="-70.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-3.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>99</objectRef>
                        </objectContingent>
                        <attributeContingent>
                        </attributeContingent>
                </concept>
                <concept id="24">
                        <position x="-20.000000" y="-80.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-3.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>100</objectRef>
                        </objectContingent>
                        <attributeContingent>
                        </attributeContingent>
                </concept>
                <concept id="25">
                        <position x="-60.000000" y="-60.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-3.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>101</objectRef>
                        </objectContingent>
                        <attributeContingent>
                                <labelStyle>
                                        <offset x="-1.400000" y="3.000000"/>
                                        <textAlignment>right</textAlignment>
                                </labelStyle>
                                <attributeRef>56</attributeRef>
                        </attributeContingent>
                </concept>
                <concept id="26">
                        <position x="-50.000000" y="-70.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-3.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>102</objectRef>
                        </objectContingent>
                        <attributeContingent>
                        </attributeContingent>
                </concept>
                <concept id="27">
                        <position x="-40.000000" y="-80.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-3.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>103</objectRef>
                        </objectContingent>
                        <attributeContingent>
                        </attributeContingent>
                </concept>
                <concept id="28">
                        <position x="-30.000000" y="-90.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-3.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>104</objectRef>
                        </objectContingent>
                        <attributeContingent>
                        </attributeContingent>
                </concept>
                <concept id="29">
                        <position x="-70.000000" y="-70.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-3.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>105</objectRef>
                        </objectContingent>
                        <attributeContingent>
                                <labelStyle>
                                        <offset x="-2.000000" y="2.800000"/>
                                        <textAlignment>right</textAlignment>
                                </labelStyle>
                                <attributeRef>57</attributeRef>
                        </attributeContingent>
                </concept>
                <concept id="30">
                        <position x="-60.000000" y="-80.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-3.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>106</objectRef>
                        </objectContingent>
                        <attributeContingent>
                        </attributeContingent>
                </concept>
                <concept id="31">
                        <position x="-50.000000" y="-90.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-3.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>107</objectRef>
                        </objectContingent>
                        <attributeContingent>
                        </attributeContingent>
                </concept>
                <concept id="32">
                        <position x="-40.000000" y="-100.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-3.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>108</objectRef>
                        </objectContingent>
                        <attributeContingent>
                        </attributeContingent>
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
        <diagram title="interne Laufwerksschächte">
                <concept id="1">
                        <position x="0.000000" y="0.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-3.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>109</objectRef>
                        </objectContingent>
                        <attributeContingent>
                                <labelStyle>
                                        <offset x="-2.900000" y="3.400000"/>
                                        <textAlignment>right</textAlignment>
                                </labelStyle>
                                <attributeRef>58</attributeRef>
                                <attributeRef>59</attributeRef>
                        </attributeContingent>
                </concept>
                <concept id="2">
                        <position x="-10.000000" y="-10.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-3.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>110</objectRef>
                        </objectContingent>
                        <attributeContingent>
                                <labelStyle>
                                        <offset x="-3.500000" y="3.000000"/>
                                        <textAlignment>right</textAlignment>
                                </labelStyle>
                                <attributeRef>60</attributeRef>
                        </attributeContingent>
                </concept>
                <concept id="3">
                        <position x="-20.000000" y="-20.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-3.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>111</objectRef>
                        </objectContingent>
                        <attributeContingent>
                                <labelStyle>
                                        <offset x="-2.600000" y="3.500000"/>
                                        <textAlignment>right</textAlignment>
                                </labelStyle>
                                <attributeRef>61</attributeRef>
                        </attributeContingent>
                </concept>
                <concept id="4">
                        <position x="-30.000000" y="-30.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-3.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>112</objectRef>
                        </objectContingent>
                        <attributeContingent>
                                <labelStyle>
                                        <offset x="-2.800000" y="3.400000"/>
                                        <textAlignment>right</textAlignment>
                                </labelStyle>
                                <attributeRef>62</attributeRef>
                        </attributeContingent>
                </concept>
                <concept id="5">
                        <position x="10.000000" y="-10.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-3.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>113</objectRef>
                        </objectContingent>
                        <attributeContingent>
                                <labelStyle>
                                        <offset x="3.000000" y="3.000000"/>
                                        <textAlignment>left</textAlignment>
                                </labelStyle>
                                <attributeRef>63</attributeRef>
                        </attributeContingent>
                </concept>
                <concept id="6">
                        <position x="0.000000" y="-20.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-3.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>114</objectRef>
                        </objectContingent>
                        <attributeContingent>
                        </attributeContingent>
                </concept>
                <concept id="7">
                        <position x="-10.000000" y="-30.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-3.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>115</objectRef>
                        </objectContingent>
                        <attributeContingent>
                        </attributeContingent>
                </concept>
                <concept id="8">
                        <position x="-20.000000" y="-40.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-3.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>116</objectRef>
                        </objectContingent>
                        <attributeContingent>
                        </attributeContingent>
                </concept>
                <concept id="9">
                        <position x="20.000000" y="-20.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-3.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>117</objectRef>
                        </objectContingent>
                        <attributeContingent>
                                <labelStyle>
                                        <offset x="3.000000" y="3.000000"/>
                                        <textAlignment>left</textAlignment>
                                </labelStyle>
                                <attributeRef>64</attributeRef>
                        </attributeContingent>
                </concept>
                <concept id="10">
                        <position x="10.000000" y="-30.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-3.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>118</objectRef>
                        </objectContingent>
                        <attributeContingent>
                        </attributeContingent>
                </concept>
                <concept id="11">
                        <position x="0.000000" y="-40.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-3.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>119</objectRef>
                        </objectContingent>
                        <attributeContingent>
                        </attributeContingent>
                </concept>
                <concept id="12">
                        <position x="-10.000000" y="-50.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-3.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>120</objectRef>
                        </objectContingent>
                        <attributeContingent>
                        </attributeContingent>
                </concept>
                <concept id="13">
                        <position x="30.000000" y="-30.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-3.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>121</objectRef>
                        </objectContingent>
                        <attributeContingent>
                                <labelStyle>
                                        <offset x="3.000000" y="3.000000"/>
                                        <textAlignment>left</textAlignment>
                                </labelStyle>
                                <attributeRef>65</attributeRef>
                        </attributeContingent>
                </concept>
                <concept id="14">
                        <position x="20.000000" y="-40.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-3.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>122</objectRef>
                        </objectContingent>
                        <attributeContingent>
                        </attributeContingent>
                </concept>
                <concept id="15">
                        <position x="10.000000" y="-50.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-3.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>123</objectRef>
                        </objectContingent>
                        <attributeContingent>
                        </attributeContingent>
                </concept>
                <concept id="16">
                        <position x="0.000000" y="-60.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-3.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>124</objectRef>
                        </objectContingent>
                        <attributeContingent>
                        </attributeContingent>
                </concept>
                <concept id="17">
                        <position x="-40.000000" y="-40.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-3.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>125</objectRef>
                        </objectContingent>
                        <attributeContingent>
                                <labelStyle>
                                        <offset x="-3.000000" y="3.300000"/>
                                        <textAlignment>right</textAlignment>
                                </labelStyle>
                                <attributeRef>66</attributeRef>
                        </attributeContingent>
                </concept>
                <concept id="18">
                        <position x="-30.000000" y="-50.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-3.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>126</objectRef>
                        </objectContingent>
                        <attributeContingent>
                        </attributeContingent>
                </concept>
                <concept id="19">
                        <position x="-20.000000" y="-60.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-3.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>127</objectRef>
                        </objectContingent>
                        <attributeContingent>
                        </attributeContingent>
                </concept>
                <concept id="20">
                        <position x="-10.000000" y="-70.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-3.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>128</objectRef>
                        </objectContingent>
                        <attributeContingent>
                        </attributeContingent>
                </concept>
                <concept id="21">
                        <position x="40.000000" y="-40.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-3.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>129</objectRef>
                        </objectContingent>
                        <attributeContingent>
                                <labelStyle>
                                        <offset x="3.000000" y="3.000000"/>
                                        <textAlignment>left</textAlignment>
                                </labelStyle>
                                <attributeRef>67</attributeRef>
                        </attributeContingent>
                </concept>
                <concept id="22">
                        <position x="30.000000" y="-50.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-3.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>130</objectRef>
                        </objectContingent>
                        <attributeContingent>
                        </attributeContingent>
                </concept>
                <concept id="23">
                        <position x="20.000000" y="-60.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-3.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>131</objectRef>
                        </objectContingent>
                        <attributeContingent>
                        </attributeContingent>
                </concept>
                <concept id="24">
                        <position x="10.000000" y="-70.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-3.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>132</objectRef>
                        </objectContingent>
                        <attributeContingent>
                        </attributeContingent>
                </concept>
                <concept id="25">
                        <position x="0.000000" y="-80.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-3.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>133</objectRef>
                        </objectContingent>
                        <attributeContingent>
                        </attributeContingent>
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
        <diagram title="Leistung des Netzteils und Zahl der Anschlüsse">
                <concept id="1">
                        <position x="0.000000" y="0.000000"/>
                        <objectContingent>
                        </objectContingent>
                        <attributeContingent>
                        </attributeContingent>
                </concept>
                <concept id="2">
                        <position x="0.000000" y="-20.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-3.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>134</objectRef>
                        </objectContingent>
                        <attributeContingent>
                                <labelStyle>
                                        <offset x="3.000000" y="3.000000"/>
                                        <textAlignment>left</textAlignment>
                                </labelStyle>
                                <attributeRef>68</attributeRef>
                        </attributeContingent>
                </concept>
                <concept id="3">
                        <position x="0.000000" y="-40.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-3.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>135</objectRef>
                        </objectContingent>
                        <attributeContingent>
                                <labelStyle>
                                        <offset x="3.000000" y="3.000000"/>
                                        <textAlignment>left</textAlignment>
                                </labelStyle>
                                <attributeRef>69</attributeRef>
                        </attributeContingent>
                </concept>
                <concept id="4">
                        <position x="0.000000" y="-60.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-3.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>136</objectRef>
                        </objectContingent>
                        <attributeContingent>
                                <labelStyle>
                                        <offset x="3.000000" y="3.000000"/>
                                        <textAlignment>left</textAlignment>
                                </labelStyle>
                                <attributeRef>70</attributeRef>
                        </attributeContingent>
                </concept>
                <concept id="5">
                        <position x="0.000000" y="-80.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-3.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>137</objectRef>
                        </objectContingent>
                        <attributeContingent>
                                <labelStyle>
                                        <offset x="3.000000" y="3.000000"/>
                                        <textAlignment>left</textAlignment>
                                </labelStyle>
                                <attributeRef>71</attributeRef>
                        </attributeContingent>
                </concept>
                <concept id="6">
                        <position x="0.000000" y="-100.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-3.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>138</objectRef>
                        </objectContingent>
                        <attributeContingent>
                                <labelStyle>
                                        <offset x="3.000000" y="3.000000"/>
                                        <textAlignment>left</textAlignment>
                                </labelStyle>
                                <attributeRef>72</attributeRef>
                        </attributeContingent>
                </concept>
                <concept id="7">
                        <position x="-60.000000" y="-20.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-3.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>139</objectRef>
                        </objectContingent>
                        <attributeContingent>
                                <labelStyle>
                                        <offset x="-3.000000" y="3.000000"/>
                                        <textAlignment>right</textAlignment>
                                </labelStyle>
                                <attributeRef>73</attributeRef>
                                <attributeRef>74</attributeRef>
                        </attributeContingent>
                </concept>
                <concept id="8">
                        <position x="20.000000" y="-20.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-3.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>140</objectRef>
                        </objectContingent>
                        <attributeContingent>
                                <labelStyle>
                                        <offset x="3.000000" y="3.000000"/>
                                        <textAlignment>left</textAlignment>
                                </labelStyle>
                                <attributeRef>75</attributeRef>
                        </attributeContingent>
                </concept>
                <concept id="9">
                        <position x="20.000000" y="-40.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-3.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>141</objectRef>
                        </objectContingent>
                        <attributeContingent>
                                <labelStyle>
                                        <offset x="3.000000" y="3.000000"/>
                                        <textAlignment>left</textAlignment>
                                </labelStyle>
                                <attributeRef>76</attributeRef>
                        </attributeContingent>
                </concept>
                <concept id="10">
                        <position x="20.000000" y="-60.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-3.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>142</objectRef>
                        </objectContingent>
                        <attributeContingent>
                                <labelStyle>
                                        <offset x="3.000000" y="3.000000"/>
                                        <textAlignment>left</textAlignment>
                                </labelStyle>
                                <attributeRef>77</attributeRef>
                        </attributeContingent>
                </concept>
                <concept id="11">
                        <position x="20.000000" y="-80.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-3.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>143</objectRef>
                        </objectContingent>
                        <attributeContingent>
                                <labelStyle>
                                        <offset x="3.000000" y="3.000000"/>
                                        <textAlignment>left</textAlignment>
                                </labelStyle>
                                <attributeRef>78</attributeRef>
                        </attributeContingent>
                </concept>
                <concept id="12">
                        <position x="20.000000" y="-100.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-3.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>144</objectRef>
                        </objectContingent>
                        <attributeContingent>
                                <labelStyle>
                                        <offset x="3.000000" y="3.000000"/>
                                        <textAlignment>left</textAlignment>
                                </labelStyle>
                                <attributeRef>79</attributeRef>
                        </attributeContingent>
                </concept>
                <concept id="13">
                        <position x="40.000000" y="-20.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-3.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>145</objectRef>
                        </objectContingent>
                        <attributeContingent>
                                <labelStyle>
                                        <offset x="3.000000" y="3.000000"/>
                                        <textAlignment>left</textAlignment>
                                </labelStyle>
                                <attributeRef>80</attributeRef>
                        </attributeContingent>
                </concept>
                <concept id="14">
                        <position x="40.000000" y="-40.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-3.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>146</objectRef>
                        </objectContingent>
                        <attributeContingent>
                                <labelStyle>
                                        <offset x="3.000000" y="3.000000"/>
                                        <textAlignment>left</textAlignment>
                                </labelStyle>
                                <attributeRef>81</attributeRef>
                        </attributeContingent>
                </concept>
                <concept id="15">
                        <position x="40.000000" y="-60.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-3.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>147</objectRef>
                        </objectContingent>
                        <attributeContingent>
                                <labelStyle>
                                        <offset x="3.000000" y="3.000000"/>
                                        <textAlignment>left</textAlignment>
                                </labelStyle>
                                <attributeRef>82</attributeRef>
                        </attributeContingent>
                </concept>
                <concept id="16">
                        <position x="40.000000" y="-80.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-3.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>148</objectRef>
                        </objectContingent>
                        <attributeContingent>
                                <labelStyle>
                                        <offset x="3.000000" y="3.000000"/>
                                        <textAlignment>left</textAlignment>
                                </labelStyle>
                                <attributeRef>83</attributeRef>
                        </attributeContingent>
                </concept>
                <concept id="17">
                        <position x="40.000000" y="-100.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-3.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>149</objectRef>
                        </objectContingent>
                        <attributeContingent>
                                <labelStyle>
                                        <offset x="3.000000" y="3.000000"/>
                                        <textAlignment>left</textAlignment>
                                </labelStyle>
                                <attributeRef>84</attributeRef>
                        </attributeContingent>
                </concept>
                <concept id="18">
                        <position x="60.000000" y="-20.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-3.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>150</objectRef>
                        </objectContingent>
                        <attributeContingent>
                                <labelStyle>
                                        <offset x="3.000000" y="3.000000"/>
                                        <textAlignment>left</textAlignment>
                                </labelStyle>
                                <attributeRef>85</attributeRef>
                        </attributeContingent>
                </concept>
                <concept id="19">
                        <position x="60.000000" y="-40.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-3.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>151</objectRef>
                        </objectContingent>
                        <attributeContingent>
                                <labelStyle>
                                        <offset x="3.000000" y="3.000000"/>
                                        <textAlignment>left</textAlignment>
                                </labelStyle>
                                <attributeRef>86</attributeRef>
                        </attributeContingent>
                </concept>
                <concept id="20">
                        <position x="60.000000" y="-60.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-3.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>152</objectRef>
                        </objectContingent>
                        <attributeContingent>
                                <labelStyle>
                                        <offset x="3.000000" y="3.000000"/>
                                        <textAlignment>left</textAlignment>
                                </labelStyle>
                                <attributeRef>87</attributeRef>
                        </attributeContingent>
                </concept>
                <concept id="21">
                        <position x="60.000000" y="-80.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-3.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>153</objectRef>
                        </objectContingent>
                        <attributeContingent>
                                <labelStyle>
                                        <offset x="3.000000" y="3.000000"/>
                                        <textAlignment>left</textAlignment>
                                </labelStyle>
                                <attributeRef>88</attributeRef>
                        </attributeContingent>
                </concept>
                <concept id="22">
                        <position x="60.000000" y="-100.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-3.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>154</objectRef>
                        </objectContingent>
                        <attributeContingent>
                                <labelStyle>
                                        <offset x="3.000000" y="3.000000"/>
                                        <textAlignment>left</textAlignment>
                                </labelStyle>
                                <attributeRef>89</attributeRef>
                        </attributeContingent>
                </concept>
                <concept id="23">
                        <position x="-20.000000" y="-20.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-3.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>155</objectRef>
                        </objectContingent>
                        <attributeContingent>
                                <labelStyle>
                                        <offset x="3.000000" y="3.000000"/>
                                        <textAlignment>left</textAlignment>
                                </labelStyle>
                                <attributeRef>90</attributeRef>
                        </attributeContingent>
                </concept>
                <concept id="24">
                        <position x="-20.000000" y="-40.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-3.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>156</objectRef>
                        </objectContingent>
                        <attributeContingent>
                                <labelStyle>
                                        <offset x="3.000000" y="3.000000"/>
                                        <textAlignment>left</textAlignment>
                                </labelStyle>
                                <attributeRef>91</attributeRef>
                        </attributeContingent>
                </concept>
                <concept id="25">
                        <position x="-20.000000" y="-60.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-3.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>157</objectRef>
                        </objectContingent>
                        <attributeContingent>
                                <labelStyle>
                                        <offset x="3.000000" y="3.000000"/>
                                        <textAlignment>left</textAlignment>
                                </labelStyle>
                                <attributeRef>92</attributeRef>
                        </attributeContingent>
                </concept>
                <concept id="26">
                        <position x="-20.000000" y="-80.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-3.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>158</objectRef>
                        </objectContingent>
                        <attributeContingent>
                                <labelStyle>
                                        <offset x="3.000000" y="3.000000"/>
                                        <textAlignment>left</textAlignment>
                                </labelStyle>
                                <attributeRef>93</attributeRef>
                        </attributeContingent>
                </concept>
                <concept id="27">
                        <position x="-20.000000" y="-100.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-3.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>159</objectRef>
                        </objectContingent>
                        <attributeContingent>
                                <labelStyle>
                                        <offset x="3.000000" y="3.000000"/>
                                        <textAlignment>left</textAlignment>
                                </labelStyle>
                                <attributeRef>94</attributeRef>
                        </attributeContingent>
                </concept>
                <concept id="28">
                        <position x="-40.000000" y="-20.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-3.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>160</objectRef>
                        </objectContingent>
                        <attributeContingent>
                                <labelStyle>
                                        <offset x="3.000000" y="3.000000"/>
                                        <textAlignment>left</textAlignment>
                                </labelStyle>
                                <attributeRef>95</attributeRef>
                        </attributeContingent>
                </concept>
                <concept id="29">
                        <position x="-40.000000" y="-40.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-3.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>161</objectRef>
                        </objectContingent>
                        <attributeContingent>
                                <labelStyle>
                                        <offset x="3.000000" y="3.000000"/>
                                        <textAlignment>left</textAlignment>
                                </labelStyle>
                                <attributeRef>96</attributeRef>
                        </attributeContingent>
                </concept>
                <concept id="30">
                        <position x="-40.000000" y="-60.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-3.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>162</objectRef>
                        </objectContingent>
                        <attributeContingent>
                                <labelStyle>
                                        <offset x="3.000000" y="3.000000"/>
                                        <textAlignment>left</textAlignment>
                                </labelStyle>
                                <attributeRef>97</attributeRef>
                        </attributeContingent>
                </concept>
                <concept id="31">
                        <position x="-40.000000" y="-80.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-3.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>163</objectRef>
                        </objectContingent>
                        <attributeContingent>
                                <labelStyle>
                                        <offset x="3.000000" y="3.000000"/>
                                        <textAlignment>left</textAlignment>
                                </labelStyle>
                                <attributeRef>98</attributeRef>
                        </attributeContingent>
                </concept>
                <concept id="32">
                        <position x="-40.000000" y="-100.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-3.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>164</objectRef>
                        </objectContingent>
                        <attributeContingent>
                                <labelStyle>
                                        <offset x="3.000000" y="3.000000"/>
                                        <textAlignment>left</textAlignment>
                                </labelStyle>
                                <attributeRef>99</attributeRef>
                        </attributeContingent>
                </concept>
                <concept id="33">
                        <position x="-60.000000" y="-40.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-3.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>165</objectRef>
                        </objectContingent>
                        <attributeContingent>
                                <labelStyle>
                                        <offset x="3.000000" y="3.000000"/>
                                        <textAlignment>left</textAlignment>
                                </labelStyle>
                                <attributeRef>100</attributeRef>
                        </attributeContingent>
                </concept>
                <concept id="34">
                        <position x="-60.000000" y="-60.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-3.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>166</objectRef>
                        </objectContingent>
                        <attributeContingent>
                                <labelStyle>
                                        <offset x="3.000000" y="3.000000"/>
                                        <textAlignment>left</textAlignment>
                                </labelStyle>
                                <attributeRef>101</attributeRef>
                        </attributeContingent>
                </concept>
                <concept id="35">
                        <position x="-60.000000" y="-80.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-3.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>167</objectRef>
                        </objectContingent>
                        <attributeContingent>
                                <labelStyle>
                                        <offset x="3.000000" y="3.000000"/>
                                        <textAlignment>left</textAlignment>
                                </labelStyle>
                                <attributeRef>102</attributeRef>
                        </attributeContingent>
                </concept>
                <concept id="36">
                        <position x="-60.000000" y="-100.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-3.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>168</objectRef>
                        </objectContingent>
                        <attributeContingent>
                                <labelStyle>
                                        <offset x="3.000000" y="3.000000"/>
                                        <textAlignment>left</textAlignment>
                                </labelStyle>
                                <attributeRef>103</attributeRef>
                        </attributeContingent>
                </concept>
                <concept id="37">
                        <position x="0.000000" y="-120.000000"/>
                        <objectContingent>
                        </objectContingent>
                        <attributeContingent>
                        </attributeContingent>
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
        <diagram title="Ports auf dem Motherboard">
                <concept id="1">
                        <position x="-10.000000" y="0.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-3.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>169</objectRef>
                        </objectContingent>
                        <attributeContingent>
                        </attributeContingent>
                </concept>
                <concept id="2">
                        <position x="-10.000000" y="-20.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-3.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>170</objectRef>
                        </objectContingent>
                        <attributeContingent>
                                <labelStyle>
                                        <offset x="-2.100000" y="3.000000"/>
                                        <textAlignment>right</textAlignment>
                                </labelStyle>
                                <attributeRef>104</attributeRef>
                                <attributeRef>105</attributeRef>
                        </attributeContingent>
                </concept>
                <concept id="3">
                        <position x="-10.000000" y="-40.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-3.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>171</objectRef>
                        </objectContingent>
                        <attributeContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="3.500000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <attributeRef>106</attributeRef>
                        </attributeContingent>
                </concept>
                <concept id="4">
                        <position x="10.000000" y="-40.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-3.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>172</objectRef>
                        </objectContingent>
                        <attributeContingent>
                                <labelStyle>
                                        <offset x="1.600000" y="2.700000"/>
                                        <textAlignment>left</textAlignment>
                                </labelStyle>
                                <attributeRef>107</attributeRef>
                        </attributeContingent>
                </concept>
                <concept id="5">
                        <position x="10.000000" y="-60.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-3.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>173</objectRef>
                        </objectContingent>
                        <attributeContingent>
                        </attributeContingent>
                </concept>
                <concept id="6">
                        <position x="-30.000000" y="-40.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-3.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>174</objectRef>
                        </objectContingent>
                        <attributeContingent>
                                <labelStyle>
                                        <offset x="-1.200000" y="3.200000"/>
                                        <textAlignment>right</textAlignment>
                                </labelStyle>
                                <attributeRef>108</attributeRef>
                        </attributeContingent>
                </concept>
                <concept id="7">
                        <position x="-30.000000" y="-60.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-3.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>175</objectRef>
                        </objectContingent>
                        <attributeContingent>
                        </attributeContingent>
                </concept>
                <concept id="8">
                        <position x="-10.000000" y="-80.000000"/>
                        <objectContingent>
                        </objectContingent>
                        <attributeContingent>
                        </attributeContingent>
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
        <diagram title="Vertriebsform">
                <concept id="1">
                        <position x="0.000000" y="0.000000"/>
                        <objectContingent>
                        </objectContingent>
                        <attributeContingent>
                        </attributeContingent>
                </concept>
                <concept id="2">
                        <position x="-10.000000" y="-10.000000"/>
                        <objectContingent>
                        </objectContingent>
                        <attributeContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="2.500000"/>
                                        <textAlignment>right</textAlignment>
                                </labelStyle>
                                <attributeRef>109</attributeRef>
                        </attributeContingent>
                </concept>
                <concept id="3">
                        <position x="-20.000000" y="-20.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-3.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>176</objectRef>
                        </objectContingent>
                        <attributeContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="2.500000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <attributeRef>110</attributeRef>
                        </attributeContingent>
                </concept>
                <concept id="4">
                        <position x="10.000000" y="-10.000000"/>
                        <objectContingent>
                        </objectContingent>
                        <attributeContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="2.500000"/>
                                        <textAlignment>left</textAlignment>
                                </labelStyle>
                                <attributeRef>111</attributeRef>
                        </attributeContingent>
                </concept>
                <concept id="5">
                        <position x="20.000000" y="-20.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-3.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>177</objectRef>
                        </objectContingent>
                        <attributeContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="2.500000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <attributeRef>112</attributeRef>
                        </attributeContingent>
                </concept>
                <concept id="6">
                        <position x="0.000000" y="-20.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="-3.000000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>178</objectRef>
                        </objectContingent>
                        <attributeContingent>
                                <labelStyle>
                                        <offset x="0.000000" y="2.500000"/>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <attributeRef>113</attributeRef>
                        </attributeContingent>
                </concept>
                <concept id="7">
                        <position x="0.000000" y="-30.000000"/>
                        <objectContingent>
                        </objectContingent>
                        <attributeContingent>
                        </attributeContingent>
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
</conceptualSchema>