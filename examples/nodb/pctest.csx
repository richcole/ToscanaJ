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
<conceptualSchema version="1.0" askDatabase="false">
	<context>
		<object id="1">Compaq Deskpro 66M</object>
		<object id="2">IBM PS/2 Model 77 486DX2</object>
		<object id="3">Everex Tempo M Series 486 DX2/66</object>
		<object id="4">FutureTech System 462E</object>
		<object id="5">HP Vectra 486/66U</object>
		<object id="6">Memorex Telex 8092-66</object>
		<object id="7">NCR System 3350</object>
		<object id="8">NEC Express DX2/66e</object>
		<object id="9">Swan 486DX2-66DB</object>
		<object id="10">Arche Legacy 486/66DX2</object>
		<object id="11">Ares 486-66DX2 VL-Bus</object>
		<object id="12">Ariel 486DX2-66VLB</object>
		<object id="13">AST Bravo 4/66d</object>
		<object id="14">BOSS 466d</object>
		<object id="15">C² Saber 486/e DX2-66</object>
		<object id="16">CompuAdd 466E</object>
		<object id="17">Dell 466DE/2</object>
		<object id="18">ZDS Z-Station 466Xh Model 200</object>
		<object id="19">ALR Flyer 32DT 4DX2/66</object>
		<object id="20">American Super Computer 486X2/e66</object>
		<object id="21">DFI 486-66DX2</object>
		<object id="22">FCS 486-66</object>
		<object id="23">GCH EasyData 486DX-2/66</object>
		<object id="24">Mega Impact 486DX2/66E+</object>
		<object id="25">Micro Express ME 486-Local Bus/DX2/66</object>
		<object id="26">National Microsystems Flash 486DX2-66E</object>
		<object id="27">Osicom i466 MOD 420</object>
		<object id="28">PCS Double Pro-66</object>
		<object id="29">QSI Klonimus 486DX2/66</object>
		<object id="30">Quill Qtech 486 4D2/66</object>
		<object id="31">Standard Windows Workstation Plus</object>
		<object id="32">Tangent Model 466ex</object>
		<object id="33">Tri-Star 66/DX2-VL</object>
		<object id="34">USA Flex 486DX2/66</object>
		<object id="35">American Mitac TL4466</object>
		<object id="36">ATronics ATI-486-66</object>
		<object id="37">Clover 486 Quick-I Series</object>
		<object id="38">Comtrade 486 EISA Dream Machine</object>
		<object id="39">Digital DECpc 466d2 LP</object>
		<object id="40">Edge 466 Magnum</object>
		<object id="41">Gecco 466E</object>
		<object id="42">Keydata 486DX2-66 KeyStation</object>
		<object id="43">Lightning ThunderBox</object>
		<object id="44">LodeStar 486-DX2/66 EISA WINstation</object>
		<object id="45">Naga Windows Workstation</object>
		<object id="46">Northgate SlimLine ZXP</object>
		<object id="47">Poly 486-66LM</object>
		<object id="48">U.S. Micro Jet 486DX2-66</object>
		<object id="49">Wyse Decision 486si</object>
		<object id="50">Austin 466DX2 WinStation</object>
		<object id="51">Bi-Link Desktop i486DX2/66</object>
		<object id="52">BLK 486DX2/66</object>
		<object id="53">Blue Star 466D2U</object>
		<object id="54">Comex 486DX2/66</object>
		<object id="55">CompuAdd Express 466DX Scalable</object>
		<object id="56">Diamond DX2-66</object>
		<object id="57">EPS ISA 486 DX2/66</object>
		<object id="58">Expo 486 dX2/66</object>
		<object id="59">Gateway 2000 4DX2-66V</object>
		<object id="60">Hyundai 466D2</object>
		<object id="61">IDS 466i2</object>
		<object id="62">Insight 486DX2-66I</object>
		<object id="63">Int. Instr. Blue Max Monolith 486D2/66UP</object>
		<object id="64">Occidental 66MHz 486DX2</object>
		<object id="65">PC Brand Leader Cache 486/DX2-66</object>
		<object id="66">PC Pros 486/66DX2 5550T</object>
		<object id="67">Silicon Pylon II 486DXi-212</object>
		<object id="68">SST 486DX2-66MWC</object>
		<object id="69">Twinhead Superset 600/462D</object>
		<object id="70">Zeos 486DX2-66</object>
		<object id="71">Broadax 486DX2-66</object>
		<object id="72">CAF Gold 6D2</object>
		<object id="73">NETiS Ultra WinStation N466L</object>
		<attribute id="1" name="&gt;=2500$"/>
		<attribute id="2" name="&gt;=3000$"/>
		<attribute id="3" name="&gt;=3500$"/>
		<attribute id="4" name="&gt;=4000$"/>
		<attribute id="5" name="&gt;=4500$"/>
		<attribute id="6" name="&gt;=5000$"/>
		<attribute id="7" name="&lt;5000$"/>
		<attribute id="8" name="&lt;4500$"/>
		<attribute id="9" name="&lt;4000$"/>
		<attribute id="10" name="&lt;3500$"/>
		<attribute id="11" name="&lt;3000$"/>
		<attribute id="12" name="&lt;2500$"/>
		<attribute id="13" name="&gt;=1500"/>
		<attribute id="14" name="&gt;=3000"/>
		<attribute id="15" name="&gt;=4500"/>
		<attribute id="16" name="&gt;=6000"/>
		<attribute id="17" name="&gt;=7500"/>
		<attribute id="18" name="&gt;=9000"/>
		<attribute id="19" name="&lt;9000"/>
		<attribute id="20" name="&lt;7500"/>
		<attribute id="21" name="&lt;6000"/>
		<attribute id="22" name="&lt;4500"/>
		<attribute id="23" name="&lt;3000"/>
		<attribute id="24" name="&lt;1500"/>
		<attribute id="25" name="&gt;=225MB"/>
		<attribute id="26" name="&gt;=250MB"/>
		<attribute id="27" name="&gt;=350MB"/>
		<attribute id="28" name="&gt;=400MB"/>
		<attribute id="29" name="&gt;=450MB"/>
		<attribute id="30" name="&gt;=500MB"/>
		<attribute id="31" name="&lt;500MB"/>
		<attribute id="32" name="&lt;450MB"/>
		<attribute id="33" name="&lt;400MB"/>
		<attribute id="34" name="&lt;350MB"/>
		<attribute id="35" name="&lt;250MB"/>
		<attribute id="36" name="&lt;225MB"/>
		<attribute id="37" name="5 Anschlüsse"/>
		<attribute id="38" name="Leistung &lt;150 Watt"/>
		<attribute id="39" name="2 Anschlüsse"/>
		<attribute id="40" name="6 Anschlüsse"/>
		<attribute id="41" name="7 Anschlüsse"/>
		<attribute id="42" name="8 Anschlüsse"/>
		<attribute id="43" name="4 Anschlüsse"/>
		<attribute id="44" name="3 Anschlüsse"/>
		<attribute id="45" name="&lt;200 Watt"/>
		<attribute id="46" name="&lt;250 Watt"/>
		<attribute id="47" name="&lt;300 Watt"/>
		<attribute id="48" name="&gt;=300 Watt"/>
		<attribute id="49" name="ISA Bus"/>
		<attribute id="50" name="EISA Bus"/>
		<attribute id="51" name="Local Bus"/>
		<attribute id="52" name="Motherboard"/>
		<attribute id="53" name="MCA"/>
		<attribute id="54" name="VESA Local Bus"/>
		<attribute id="55" name="Proprietary Local Bus"/>
		<attribute id="56" name="UBSA Local BUS"/>
		<attribute id="57" name="kein 5¼&quot; Schacht"/>
		<attribute id="58" name="kein 3½&quot; Schacht"/>
		<attribute id="59" name="ein 3½&quot; Schacht"/>
		<attribute id="60" name="zwei 3½&quot; Schächte"/>
		<attribute id="61" name="drei 3½&quot; Schächte"/>
		<attribute id="62" name="ein 5¼&quot; Schacht"/>
		<attribute id="63" name="zwei 5¼&quot; Schächte"/>
		<attribute id="64" name="drei 5¼&quot; Schächte"/>
		<attribute id="65" name="vier 5¼&quot; Schächte"/>
		<attribute id="66" name="fünf 5¼&quot; Schächte"/>
		<attribute id="67" name="sechs 5¼&quot; Schächte"/>
		<attribute id="68" name="sieben 5¼&quot; Schächte"/>
		<attribute id="69" name="ISA-Bus"/>
		<attribute id="70" name="EISA-Bus"/>
		<attribute id="71" name="MCA-Bus"/>
		<attribute id="72" name="Desktop"/>
		<attribute id="73" name="Slimline"/>
		<attribute id="74" name="Small-footprint"/>
		<attribute id="75" name="Tower"/>
		<attribute id="76" name="Mini-Tower"/>
		<attribute id="77" name="Disk WinMark &gt; 0, Graphics WinMark &gt; 0"/>
		<attribute id="78" name="&gt; 5"/>
		<attribute id="79" name="&gt; 10"/>
		<attribute id="80" name="&gt; 15"/>
		<attribute id="81" name="&gt; 20"/>
		<attribute id="82" name="&gt; 30"/>
		<attribute id="83" name="&gt; 40"/>
		<attribute id="84" name="&gt; 45"/>
		<attribute id="85" name="&gt; 60"/>
		<attribute id="86" name="&gt; 90"/>
		<attribute id="87" name="&gt; 120"/>
		<attribute id="88" name="Direktvertrieb"/>
		<attribute id="89" name="Nur Direktvertrieb"/>
		<attribute id="90" name="Händler"/>
		<attribute id="91" name="Nur Händlervertrieb"/>
		<attribute id="92" name="Beide Vertriebsformen"/>
		<attribute id="93" name="&gt;=200MB"/>
		<attribute id="94" name="Disk WinMark &gt; 0, DOSmark &gt;= 0"/>
		<attribute id="95" name="&gt;= 40"/>
		<attribute id="96" name="&gt;= 50"/>
		<attribute id="97" name="&gt;= 60"/>
		<attribute id="98" name="&gt;= 70"/>
		<attribute id="99" name="&gt;= 80"/>
		<attribute id="100" name="&gt;= 90"/>
		<attribute id="101" name="Ein paraller Port"/>
		<attribute id="102" name="Ein serieller Port"/>
		<attribute id="103" name="ein Mouse-Port"/>
		<attribute id="104" name="Zwei serielle Ports"/>
		<attribute id="105" name="Zwei parallele Ports"/>
		<attribute id="106" name="ein 5½&quot; Schacht"/>
		<attribute id="107" name="zwei 5½&quot; Schächte"/>
		<attribute id="108" name="drei 5½&quot; Schächte"/>
		<attribute id="109" name="ein 3¼&quot; Schacht"/>
		<attribute id="110" name="zwei 3¼&quot; Schächte"/>
		<attribute id="111" name="drei 3¼&quot; Schächte"/>
		<attribute id="112" name="vier 5½&quot; Schächte"/>
		<attribute id="113" name="vier 3¼&quot; Schächte"/>
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
				<attributeRef>1</attributeRef>
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
				<attributeRef>2</attributeRef>
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
				<attributeRef>3</attributeRef>
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
				<attributeRef>4</attributeRef>
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
				<attributeRef>5</attributeRef>
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
				<objectRef>2</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="3.000000" y="3.000000"/>
					<textAlignment>left</textAlignment>
				</labelStyle>
				<attributeRef>6</attributeRef>
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
				<attributeRef>7</attributeRef>
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
				<objectRef>3</objectRef>
				<objectRef>4</objectRef>
				<objectRef>5</objectRef>
				<objectRef>6</objectRef>
				<objectRef>7</objectRef>
				<objectRef>8</objectRef>
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
				<attributeRef>8</attributeRef>
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
				<objectRef>11</objectRef>
				<objectRef>12</objectRef>
				<objectRef>13</objectRef>
				<objectRef>14</objectRef>
				<objectRef>15</objectRef>
				<objectRef>16</objectRef>
				<objectRef>17</objectRef>
				<objectRef>18</objectRef>
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
				<attributeRef>9</attributeRef>
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
				<objectRef>19</objectRef>
				<objectRef>20</objectRef>
				<objectRef>21</objectRef>
				<objectRef>22</objectRef>
				<objectRef>23</objectRef>
				<objectRef>24</objectRef>
				<objectRef>25</objectRef>
				<objectRef>26</objectRef>
				<objectRef>27</objectRef>
				<objectRef>28</objectRef>
				<objectRef>29</objectRef>
				<objectRef>30</objectRef>
				<objectRef>31</objectRef>
				<objectRef>32</objectRef>
				<objectRef>33</objectRef>
				<objectRef>34</objectRef>
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
				<attributeRef>10</attributeRef>
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
				<objectRef>35</objectRef>
				<objectRef>36</objectRef>
				<objectRef>37</objectRef>
				<objectRef>38</objectRef>
				<objectRef>39</objectRef>
				<objectRef>40</objectRef>
				<objectRef>41</objectRef>
				<objectRef>42</objectRef>
				<objectRef>43</objectRef>
				<objectRef>44</objectRef>
				<objectRef>45</objectRef>
				<objectRef>46</objectRef>
				<objectRef>47</objectRef>
				<objectRef>48</objectRef>
				<objectRef>49</objectRef>
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
				<attributeRef>11</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="27">
			<position x="-80.000000" y="-120.000000"/>
			<objectContingent>
				<labelStyle>
					<offset x="0.000000" y="-4.000000"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>50</objectRef>
				<objectRef>51</objectRef>
				<objectRef>52</objectRef>
				<objectRef>53</objectRef>
				<objectRef>54</objectRef>
				<objectRef>55</objectRef>
				<objectRef>56</objectRef>
				<objectRef>57</objectRef>
				<objectRef>58</objectRef>
				<objectRef>59</objectRef>
				<objectRef>60</objectRef>
				<objectRef>61</objectRef>
				<objectRef>62</objectRef>
				<objectRef>63</objectRef>
				<objectRef>64</objectRef>
				<objectRef>65</objectRef>
				<objectRef>66</objectRef>
				<objectRef>67</objectRef>
				<objectRef>68</objectRef>
				<objectRef>69</objectRef>
				<objectRef>70</objectRef>
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
				<objectRef>71</objectRef>
				<objectRef>72</objectRef>
				<objectRef>73</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="-3.000000" y="3.000000"/>
					<textAlignment>right</textAlignment>
				</labelStyle>
				<attributeRef>12</attributeRef>
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
	<diagram title="Video (1000 Operationen je Sekunde)">
		<concept id="1">
			<position x="0.000000" y="0.000000"/>
			<objectContingent/>
			<attributeContingent>
				<labelStyle>
					<offset x="-3.000000" y="3.000000"/>
					<textAlignment>right</textAlignment>
				</labelStyle>
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
				<attributeRef>13</attributeRef>
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
				<attributeRef>14</attributeRef>
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
				<attributeRef>15</attributeRef>
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
				<attributeRef>16</attributeRef>
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
				<attributeRef>17</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="7">
			<position x="120.000000" y="-120.000000"/>
			<objectContingent>
				<labelStyle>
					<offset x="0.000000" y="-4.000000"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>55</objectRef>
				<objectRef>63</objectRef>
				<objectRef>25</objectRef>
				<objectRef>73</objectRef>
				<objectRef>49</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="3.000000" y="3.000000"/>
					<textAlignment>left</textAlignment>
				</labelStyle>
				<attributeRef>18</attributeRef>
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
				<attributeRef>19</attributeRef>
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
				<attributeRef>20</attributeRef>
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
				<objectRef>12</objectRef>
				<objectRef>50</objectRef>
				<objectRef>1</objectRef>
				<objectRef>59</objectRef>
				<objectRef>60</objectRef>
				<objectRef>43</objectRef>
				<objectRef>46</objectRef>
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
				<attributeRef>21</attributeRef>
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
				<objectRef>19</objectRef>
				<objectRef>10</objectRef>
				<objectRef>15</objectRef>
				<objectRef>16</objectRef>
				<objectRef>62</objectRef>
				<objectRef>7</objectRef>
				<objectRef>8</objectRef>
				<objectRef>64</objectRef>
				<objectRef>67</objectRef>
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
				<attributeRef>22</attributeRef>
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
				<objectRef>11</objectRef>
				<objectRef>13</objectRef>
				<objectRef>53</objectRef>
				<objectRef>14</objectRef>
				<objectRef>38</objectRef>
				<objectRef>21</objectRef>
				<objectRef>3</objectRef>
				<objectRef>41</objectRef>
				<objectRef>2</objectRef>
				<objectRef>42</objectRef>
				<objectRef>45</objectRef>
				<objectRef>65</objectRef>
				<objectRef>32</objectRef>
				<objectRef>69</objectRef>
				<objectRef>18</objectRef>
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
				<attributeRef>23</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="27">
			<position x="-80.000000" y="-120.000000"/>
			<objectContingent>
				<labelStyle>
					<offset x="0.000000" y="-4.000000"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>20</objectRef>
				<objectRef>36</objectRef>
				<objectRef>51</objectRef>
				<objectRef>52</objectRef>
				<objectRef>71</objectRef>
				<objectRef>72</objectRef>
				<objectRef>37</objectRef>
				<objectRef>54</objectRef>
				<objectRef>17</objectRef>
				<objectRef>56</objectRef>
				<objectRef>39</objectRef>
				<objectRef>40</objectRef>
				<objectRef>57</objectRef>
				<objectRef>58</objectRef>
				<objectRef>22</objectRef>
				<objectRef>4</objectRef>
				<objectRef>5</objectRef>
				<objectRef>61</objectRef>
				<objectRef>44</objectRef>
				<objectRef>24</objectRef>
				<objectRef>6</objectRef>
				<objectRef>26</objectRef>
				<objectRef>66</objectRef>
				<objectRef>28</objectRef>
				<objectRef>47</objectRef>
				<objectRef>29</objectRef>
				<objectRef>31</objectRef>
				<objectRef>9</objectRef>
				<objectRef>33</objectRef>
				<objectRef>34</objectRef>
				<objectRef>70</objectRef>
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
				<objectRef>35</objectRef>
				<objectRef>23</objectRef>
				<objectRef>27</objectRef>
				<objectRef>30</objectRef>
				<objectRef>68</objectRef>
				<objectRef>48</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="-3.000000" y="3.000000"/>
					<textAlignment>right</textAlignment>
				</labelStyle>
				<attributeRef>24</attributeRef>
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
				<attributeRef>25</attributeRef>
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
				<attributeRef>26</attributeRef>
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
				<attributeRef>27</attributeRef>
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
				<attributeRef>28</attributeRef>
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
				<attributeRef>29</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="7">
			<position x="120.000000" y="-120.000000"/>
			<objectContingent>
				<labelStyle>
					<offset x="0.000000" y="-4.000000"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>12</objectRef>
				<objectRef>8</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="3.000000" y="3.000000"/>
					<textAlignment>left</textAlignment>
				</labelStyle>
				<attributeRef>30</attributeRef>
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
				<attributeRef>31</attributeRef>
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
				<objectRef>62</objectRef>
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
				<attributeRef>32</attributeRef>
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
				<objectRef>4</objectRef>
				<objectRef>2</objectRef>
				<objectRef>27</objectRef>
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
				<attributeRef>33</attributeRef>
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
				<objectRef>35</objectRef>
				<objectRef>15</objectRef>
				<objectRef>61</objectRef>
				<objectRef>43</objectRef>
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
				<attributeRef>34</attributeRef>
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
				<objectRef>13</objectRef>
				<objectRef>36</objectRef>
				<objectRef>53</objectRef>
				<objectRef>57</objectRef>
				<objectRef>22</objectRef>
				<objectRef>59</objectRef>
				<objectRef>23</objectRef>
				<objectRef>42</objectRef>
				<objectRef>24</objectRef>
				<objectRef>45</objectRef>
				<objectRef>26</objectRef>
				<objectRef>7</objectRef>
				<objectRef>66</objectRef>
				<objectRef>47</objectRef>
				<objectRef>29</objectRef>
				<objectRef>30</objectRef>
				<objectRef>9</objectRef>
				<objectRef>32</objectRef>
				<objectRef>33</objectRef>
				<objectRef>48</objectRef>
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
				<attributeRef>35</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="27">
			<position x="-80.000000" y="-120.000000"/>
			<objectContingent>
				<labelStyle>
					<offset x="0.000000" y="-4.000000"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>20</objectRef>
				<objectRef>11</objectRef>
				<objectRef>14</objectRef>
				<objectRef>37</objectRef>
				<objectRef>17</objectRef>
				<objectRef>56</objectRef>
				<objectRef>39</objectRef>
				<objectRef>3</objectRef>
				<objectRef>58</objectRef>
				<objectRef>41</objectRef>
				<objectRef>5</objectRef>
				<objectRef>25</objectRef>
				<objectRef>46</objectRef>
				<objectRef>64</objectRef>
				<objectRef>28</objectRef>
				<objectRef>31</objectRef>
				<objectRef>34</objectRef>
				<objectRef>70</objectRef>
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
				<objectRef>19</objectRef>
				<objectRef>10</objectRef>
				<objectRef>50</objectRef>
				<objectRef>51</objectRef>
				<objectRef>52</objectRef>
				<objectRef>71</objectRef>
				<objectRef>72</objectRef>
				<objectRef>54</objectRef>
				<objectRef>1</objectRef>
				<objectRef>16</objectRef>
				<objectRef>55</objectRef>
				<objectRef>38</objectRef>
				<objectRef>21</objectRef>
				<objectRef>40</objectRef>
				<objectRef>60</objectRef>
				<objectRef>63</objectRef>
				<objectRef>44</objectRef>
				<objectRef>6</objectRef>
				<objectRef>73</objectRef>
				<objectRef>65</objectRef>
				<objectRef>67</objectRef>
				<objectRef>68</objectRef>
				<objectRef>69</objectRef>
				<objectRef>49</objectRef>
				<objectRef>18</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="-3.000000" y="3.000000"/>
					<textAlignment>right</textAlignment>
				</labelStyle>
				<attributeRef>36</attributeRef>
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
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="9" y="9"/>
					<textAlignment>left</textAlignment>
				</labelStyle>
				<attributeRef>37</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="3">
			<position x="0" y="-120"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-9"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>55</objectRef>
				<objectRef>69</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="9" y="9"/>
					<textAlignment>left</textAlignment>
				</labelStyle>
			</attributeContingent>
		</concept>
		<concept id="4">
			<position x="0" y="-180"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-9"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>50</objectRef>
				<objectRef>71</objectRef>
				<objectRef>72</objectRef>
				<objectRef>16</objectRef>
				<objectRef>38</objectRef>
				<objectRef>40</objectRef>
				<objectRef>57</objectRef>
				<objectRef>59</objectRef>
				<objectRef>5</objectRef>
				<objectRef>61</objectRef>
				<objectRef>6</objectRef>
				<objectRef>25</objectRef>
				<objectRef>73</objectRef>
				<objectRef>27</objectRef>
				<objectRef>65</objectRef>
				<objectRef>30</objectRef>
				<objectRef>9</objectRef>
				<objectRef>48</objectRef>
				<objectRef>49</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="9" y="9"/>
					<textAlignment>left</textAlignment>
				</labelStyle>
			</attributeContingent>
		</concept>
		<concept id="5">
			<position x="0" y="-240"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-9"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>11</objectRef>
				<objectRef>52</objectRef>
				<objectRef>54</objectRef>
				<objectRef>21</objectRef>
				<objectRef>41</objectRef>
				<objectRef>24</objectRef>
				<objectRef>47</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="9" y="9"/>
					<textAlignment>left</textAlignment>
				</labelStyle>
			</attributeContingent>
		</concept>
		<concept id="6">
			<position x="0" y="-300"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-9"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>35</objectRef>
				<objectRef>58</objectRef>
				<objectRef>26</objectRef>
				<objectRef>70</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="9" y="9"/>
					<textAlignment>left</textAlignment>
				</labelStyle>
			</attributeContingent>
		</concept>
		<concept id="7">
			<position x="-180" y="-60"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-9"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="-9" y="9"/>
					<textAlignment>right</textAlignment>
				</labelStyle>
				<attributeRef>38</attributeRef>
				<attributeRef>39</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="8">
			<position x="60" y="-60"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-9"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="9" y="9"/>
					<textAlignment>left</textAlignment>
				</labelStyle>
				<attributeRef>40</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="9">
			<position x="60" y="-120"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-9"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="9" y="9"/>
					<textAlignment>left</textAlignment>
				</labelStyle>
			</attributeContingent>
		</concept>
		<concept id="10">
			<position x="60" y="-180"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-9"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>20</objectRef>
				<objectRef>60</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="9" y="9"/>
					<textAlignment>left</textAlignment>
				</labelStyle>
			</attributeContingent>
		</concept>
		<concept id="11">
			<position x="60" y="-240"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-9"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>15</objectRef>
				<objectRef>43</objectRef>
				<objectRef>44</objectRef>
				<objectRef>8</objectRef>
				<objectRef>34</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="9" y="9"/>
					<textAlignment>left</textAlignment>
				</labelStyle>
			</attributeContingent>
		</concept>
		<concept id="12">
			<position x="60" y="-300"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-9"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>51</objectRef>
				<objectRef>14</objectRef>
				<objectRef>22</objectRef>
				<objectRef>4</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="9" y="9"/>
					<textAlignment>left</textAlignment>
				</labelStyle>
			</attributeContingent>
		</concept>
		<concept id="13">
			<position x="120" y="-60"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-9"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="9" y="9"/>
					<textAlignment>left</textAlignment>
				</labelStyle>
				<attributeRef>41</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="14">
			<position x="120" y="-120"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-9"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="9" y="9"/>
					<textAlignment>left</textAlignment>
				</labelStyle>
			</attributeContingent>
		</concept>
		<concept id="15">
			<position x="120" y="-180"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-9"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>64</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="9" y="9"/>
					<textAlignment>left</textAlignment>
				</labelStyle>
			</attributeContingent>
		</concept>
		<concept id="16">
			<position x="120" y="-240"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-9"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>63</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="9" y="9"/>
					<textAlignment>left</textAlignment>
				</labelStyle>
			</attributeContingent>
		</concept>
		<concept id="17">
			<position x="120" y="-300"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-9"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="9" y="9"/>
					<textAlignment>left</textAlignment>
				</labelStyle>
			</attributeContingent>
		</concept>
		<concept id="18">
			<position x="180" y="-60"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-9"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="9" y="9"/>
					<textAlignment>left</textAlignment>
				</labelStyle>
				<attributeRef>42</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="19">
			<position x="180" y="-120"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-9"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="9" y="9"/>
					<textAlignment>left</textAlignment>
				</labelStyle>
			</attributeContingent>
		</concept>
		<concept id="20">
			<position x="180" y="-180"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-9"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="9" y="9"/>
					<textAlignment>left</textAlignment>
				</labelStyle>
			</attributeContingent>
		</concept>
		<concept id="21">
			<position x="180" y="-240"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-9"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="9" y="9"/>
					<textAlignment>left</textAlignment>
				</labelStyle>
			</attributeContingent>
		</concept>
		<concept id="22">
			<position x="180" y="-300"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-9"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>33</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="9" y="9"/>
					<textAlignment>left</textAlignment>
				</labelStyle>
			</attributeContingent>
		</concept>
		<concept id="23">
			<position x="-60" y="-60"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-9"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>19</objectRef>
				<objectRef>13</objectRef>
				<objectRef>39</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="9" y="9"/>
					<textAlignment>left</textAlignment>
				</labelStyle>
				<attributeRef>43</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="24">
			<position x="-60" y="-120"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-9"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>7</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="9" y="9"/>
					<textAlignment>left</textAlignment>
				</labelStyle>
			</attributeContingent>
		</concept>
		<concept id="25">
			<position x="-60" y="-180"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-9"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>10</objectRef>
				<objectRef>36</objectRef>
				<objectRef>37</objectRef>
				<objectRef>1</objectRef>
				<objectRef>17</objectRef>
				<objectRef>56</objectRef>
				<objectRef>3</objectRef>
				<objectRef>23</objectRef>
				<objectRef>2</objectRef>
				<objectRef>62</objectRef>
				<objectRef>45</objectRef>
				<objectRef>28</objectRef>
				<objectRef>29</objectRef>
				<objectRef>67</objectRef>
				<objectRef>31</objectRef>
				<objectRef>32</objectRef>
				<objectRef>18</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="9" y="9"/>
					<textAlignment>left</textAlignment>
				</labelStyle>
			</attributeContingent>
		</concept>
		<concept id="26">
			<position x="-60" y="-240"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-9"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>53</objectRef>
				<objectRef>42</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="9" y="9"/>
					<textAlignment>left</textAlignment>
				</labelStyle>
			</attributeContingent>
		</concept>
		<concept id="27">
			<position x="-60" y="-300"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-9"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>12</objectRef>
				<objectRef>66</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="9" y="9"/>
					<textAlignment>left</textAlignment>
				</labelStyle>
			</attributeContingent>
		</concept>
		<concept id="28">
			<position x="-120" y="-60"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-9"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="9" y="9"/>
					<textAlignment>left</textAlignment>
				</labelStyle>
				<attributeRef>44</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="29">
			<position x="-120" y="-120"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-9"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>46</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="9" y="9"/>
					<textAlignment>left</textAlignment>
				</labelStyle>
			</attributeContingent>
		</concept>
		<concept id="30">
			<position x="-120" y="-180"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-9"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="9" y="9"/>
					<textAlignment>left</textAlignment>
				</labelStyle>
			</attributeContingent>
		</concept>
		<concept id="31">
			<position x="-120" y="-240"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-9"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="9" y="9"/>
					<textAlignment>left</textAlignment>
				</labelStyle>
			</attributeContingent>
		</concept>
		<concept id="32">
			<position x="-120" y="-300"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-9"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="9" y="9"/>
					<textAlignment>left</textAlignment>
				</labelStyle>
			</attributeContingent>
		</concept>
		<concept id="33">
			<position x="-180" y="-120"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-9"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="9" y="9"/>
					<textAlignment>left</textAlignment>
				</labelStyle>
				<attributeRef>45</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="34">
			<position x="-180" y="-180"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-9"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>68</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="9" y="9"/>
					<textAlignment>left</textAlignment>
				</labelStyle>
				<attributeRef>46</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="35">
			<position x="-180" y="-240"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-9"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="9" y="9"/>
					<textAlignment>left</textAlignment>
				</labelStyle>
				<attributeRef>47</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="36">
			<position x="-180" y="-300"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-9"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="9" y="9"/>
					<textAlignment>left</textAlignment>
				</labelStyle>
				<attributeRef>48</attributeRef>
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
				<objectRef>19</objectRef>
				<objectRef>10</objectRef>
				<objectRef>11</objectRef>
				<objectRef>36</objectRef>
				<objectRef>52</objectRef>
				<objectRef>53</objectRef>
				<objectRef>14</objectRef>
				<objectRef>71</objectRef>
				<objectRef>15</objectRef>
				<objectRef>37</objectRef>
				<objectRef>54</objectRef>
				<objectRef>38</objectRef>
				<objectRef>17</objectRef>
				<objectRef>56</objectRef>
				<objectRef>57</objectRef>
				<objectRef>58</objectRef>
				<objectRef>4</objectRef>
				<objectRef>61</objectRef>
				<objectRef>62</objectRef>
				<objectRef>42</objectRef>
				<objectRef>44</objectRef>
				<objectRef>24</objectRef>
				<objectRef>6</objectRef>
				<objectRef>45</objectRef>
				<objectRef>27</objectRef>
				<objectRef>66</objectRef>
				<objectRef>28</objectRef>
				<objectRef>29</objectRef>
				<objectRef>30</objectRef>
				<objectRef>67</objectRef>
				<objectRef>68</objectRef>
				<objectRef>31</objectRef>
				<objectRef>32</objectRef>
				<objectRef>48</objectRef>
				<objectRef>34</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="0" y="8.75"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<attributeRef>49</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="3">
			<position x="-280" y="-175"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-10.5"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>35</objectRef>
				<objectRef>20</objectRef>
				<objectRef>1</objectRef>
				<objectRef>16</objectRef>
				<objectRef>23</objectRef>
				<objectRef>41</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="0" y="8.75"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<attributeRef>50</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="4">
			<position x="-70" y="-70"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-10.5"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>63</objectRef>
				<objectRef>25</objectRef>
				<objectRef>26</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="0" y="8.75"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<attributeRef>51</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="5">
			<position x="70" y="-140"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-10.5"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>13</objectRef>
				<objectRef>72</objectRef>
				<objectRef>55</objectRef>
				<objectRef>3</objectRef>
				<objectRef>22</objectRef>
				<objectRef>59</objectRef>
				<objectRef>5</objectRef>
				<objectRef>7</objectRef>
				<objectRef>8</objectRef>
				<objectRef>46</objectRef>
				<objectRef>65</objectRef>
				<objectRef>69</objectRef>
				<objectRef>18</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="0" y="8.75"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<attributeRef>52</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="6">
			<position x="-210" y="-140"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-10.5"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>2</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="0" y="8.75"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<attributeRef>53</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="7">
			<position x="0" y="-140"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-10.5"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>12</objectRef>
				<objectRef>50</objectRef>
				<objectRef>60</objectRef>
				<objectRef>43</objectRef>
				<objectRef>64</objectRef>
				<objectRef>33</objectRef>
				<objectRef>70</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="0" y="8.75"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<attributeRef>54</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="8">
			<position x="-140" y="-140"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-10.5"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>51</objectRef>
				<objectRef>39</objectRef>
				<objectRef>40</objectRef>
				<objectRef>73</objectRef>
				<objectRef>47</objectRef>
				<objectRef>9</objectRef>
				<objectRef>49</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="0" y="8.75"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<attributeRef>55</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="9">
			<position x="-70" y="-140"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-10.5"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>21</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="0" y="8.75"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<attributeRef>56</attributeRef>
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
	<diagram title="Zugängliche Laufwerke">
		<concept id="1">
			<position x="0" y="0"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-15"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="-17.5" y="12"/>
					<textAlignment>right</textAlignment>
				</labelStyle>
				<attributeRef>57</attributeRef>
				<attributeRef>58</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="2">
			<position x="50" y="-50"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-15"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="15" y="15"/>
					<textAlignment>left</textAlignment>
				</labelStyle>
				<attributeRef>59</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="3">
			<position x="100" y="-100"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-15"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>39</objectRef>
				<objectRef>18</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="15" y="15"/>
					<textAlignment>left</textAlignment>
				</labelStyle>
				<attributeRef>60</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="4">
			<position x="150" y="-150"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-15"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="15" y="15"/>
					<textAlignment>left</textAlignment>
				</labelStyle>
				<attributeRef>61</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="5">
			<position x="-50" y="-50"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-15"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="-4.5" y="14"/>
					<textAlignment>right</textAlignment>
				</labelStyle>
				<attributeRef>62</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="6">
			<position x="0" y="-100"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-15"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>19</objectRef>
				<objectRef>55</objectRef>
				<objectRef>7</objectRef>
				<objectRef>73</objectRef>
				<objectRef>46</objectRef>
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
				<objectRef>10</objectRef>
				<objectRef>13</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="-5.5" y="15"/>
					<textAlignment>right</textAlignment>
				</labelStyle>
				<attributeRef>63</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="10">
			<position x="-50" y="-150"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-15"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>11</objectRef>
				<objectRef>53</objectRef>
				<objectRef>54</objectRef>
				<objectRef>16</objectRef>
				<objectRef>57</objectRef>
				<objectRef>5</objectRef>
				<objectRef>8</objectRef>
				<objectRef>69</objectRef>
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
				<objectRef>52</objectRef>
				<objectRef>43</objectRef>
				<objectRef>65</objectRef>
				<objectRef>70</objectRef>
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
				<objectRef>49</objectRef>
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
				<objectRef>50</objectRef>
				<objectRef>71</objectRef>
				<objectRef>37</objectRef>
				<objectRef>1</objectRef>
				<objectRef>17</objectRef>
				<objectRef>40</objectRef>
				<objectRef>3</objectRef>
				<objectRef>22</objectRef>
				<objectRef>61</objectRef>
				<objectRef>6</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="-11" y="14"/>
					<textAlignment>right</textAlignment>
				</labelStyle>
				<attributeRef>64</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="14">
			<position x="-100" y="-200"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-15"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>23</objectRef>
				<objectRef>2</objectRef>
				<objectRef>62</objectRef>
				<objectRef>27</objectRef>
				<objectRef>28</objectRef>
				<objectRef>29</objectRef>
				<objectRef>68</objectRef>
				<objectRef>31</objectRef>
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
				<objectRef>36</objectRef>
				<objectRef>14</objectRef>
				<objectRef>72</objectRef>
				<objectRef>56</objectRef>
				<objectRef>59</objectRef>
				<objectRef>60</objectRef>
				<objectRef>25</objectRef>
				<objectRef>45</objectRef>
				<objectRef>47</objectRef>
				<objectRef>30</objectRef>
				<objectRef>32</objectRef>
				<objectRef>48</objectRef>
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
				<objectRef>38</objectRef>
				<objectRef>67</objectRef>
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
				<objectRef>21</objectRef>
				<objectRef>41</objectRef>
				<objectRef>63</objectRef>
				<objectRef>24</objectRef>
				<objectRef>64</objectRef>
				<objectRef>33</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="-8" y="16"/>
					<textAlignment>right</textAlignment>
				</labelStyle>
				<attributeRef>65</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="18">
			<position x="-150" y="-250"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-15"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>20</objectRef>
				<objectRef>15</objectRef>
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
				<objectRef>44</objectRef>
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
				<objectRef>12</objectRef>
				<objectRef>51</objectRef>
				<objectRef>26</objectRef>
				<objectRef>9</objectRef>
				<objectRef>34</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="-7.5" y="12"/>
					<textAlignment>right</textAlignment>
				</labelStyle>
				<attributeRef>66</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="22">
			<position x="-200" y="-300"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-15"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
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
				<objectRef>42</objectRef>
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
				<objectRef>58</objectRef>
				<objectRef>4</objectRef>
				<objectRef>66</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="-7" y="15"/>
					<textAlignment>right</textAlignment>
				</labelStyle>
				<attributeRef>67</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="26">
			<position x="-250" y="-350"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-15"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
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
				<objectRef>35</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="-10" y="14"/>
					<textAlignment>right</textAlignment>
				</labelStyle>
				<attributeRef>68</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="30">
			<position x="-300" y="-400"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-15"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
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
	<diagram title="Bustypen der 486/66 PCs">
		<concept id="1">
			<position x="0" y="0"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-20"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
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
				<objectRef>19</objectRef>
				<objectRef>10</objectRef>
				<objectRef>11</objectRef>
				<objectRef>12</objectRef>
				<objectRef>13</objectRef>
				<objectRef>36</objectRef>
				<objectRef>50</objectRef>
				<objectRef>51</objectRef>
				<objectRef>52</objectRef>
				<objectRef>53</objectRef>
				<objectRef>14</objectRef>
				<objectRef>71</objectRef>
				<objectRef>72</objectRef>
				<objectRef>37</objectRef>
				<objectRef>54</objectRef>
				<objectRef>55</objectRef>
				<objectRef>21</objectRef>
				<objectRef>39</objectRef>
				<objectRef>57</objectRef>
				<objectRef>3</objectRef>
				<objectRef>58</objectRef>
				<objectRef>22</objectRef>
				<objectRef>59</objectRef>
				<objectRef>23</objectRef>
				<objectRef>60</objectRef>
				<objectRef>61</objectRef>
				<objectRef>62</objectRef>
				<objectRef>63</objectRef>
				<objectRef>43</objectRef>
				<objectRef>6</objectRef>
				<objectRef>25</objectRef>
				<objectRef>45</objectRef>
				<objectRef>73</objectRef>
				<objectRef>46</objectRef>
				<objectRef>64</objectRef>
				<objectRef>27</objectRef>
				<objectRef>65</objectRef>
				<objectRef>66</objectRef>
				<objectRef>28</objectRef>
				<objectRef>47</objectRef>
				<objectRef>29</objectRef>
				<objectRef>30</objectRef>
				<objectRef>67</objectRef>
				<objectRef>68</objectRef>
				<objectRef>9</objectRef>
				<objectRef>33</objectRef>
				<objectRef>69</objectRef>
				<objectRef>48</objectRef>
				<objectRef>49</objectRef>
				<objectRef>18</objectRef>
				<objectRef>70</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="12" y="12"/>
					<textAlignment>left</textAlignment>
				</labelStyle>
				<attributeRef>69</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="3">
			<position x="80" y="-160"/>
			<objectContingent>
				<labelStyle>
					<offset x="12" y="-12"/>
					<textAlignment>left</textAlignment>
				</labelStyle>
				<objectRef>35</objectRef>
				<objectRef>20</objectRef>
				<objectRef>15</objectRef>
				<objectRef>1</objectRef>
				<objectRef>16</objectRef>
				<objectRef>38</objectRef>
				<objectRef>17</objectRef>
				<objectRef>56</objectRef>
				<objectRef>40</objectRef>
				<objectRef>4</objectRef>
				<objectRef>41</objectRef>
				<objectRef>5</objectRef>
				<objectRef>42</objectRef>
				<objectRef>44</objectRef>
				<objectRef>24</objectRef>
				<objectRef>26</objectRef>
				<objectRef>8</objectRef>
				<objectRef>31</objectRef>
				<objectRef>32</objectRef>
				<objectRef>34</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="12" y="12"/>
					<textAlignment>left</textAlignment>
				</labelStyle>
				<attributeRef>70</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="4">
			<position x="-80" y="-120"/>
			<objectContingent>
				<labelStyle>
					<offset x="-12" y="-12"/>
					<textAlignment>right</textAlignment>
				</labelStyle>
				<objectRef>2</objectRef>
				<objectRef>7</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="-12" y="12"/>
					<textAlignment>right</textAlignment>
				</labelStyle>
				<attributeRef>71</attributeRef>
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
				<objectRef>11</objectRef>
				<objectRef>50</objectRef>
				<objectRef>53</objectRef>
				<objectRef>71</objectRef>
				<objectRef>72</objectRef>
				<objectRef>54</objectRef>
				<objectRef>1</objectRef>
				<objectRef>16</objectRef>
				<objectRef>3</objectRef>
				<objectRef>59</objectRef>
				<objectRef>5</objectRef>
				<objectRef>2</objectRef>
				<objectRef>62</objectRef>
				<objectRef>63</objectRef>
				<objectRef>43</objectRef>
				<objectRef>45</objectRef>
				<objectRef>8</objectRef>
				<objectRef>73</objectRef>
				<objectRef>65</objectRef>
				<objectRef>28</objectRef>
				<objectRef>68</objectRef>
				<objectRef>31</objectRef>
				<objectRef>9</objectRef>
				<objectRef>48</objectRef>
				<objectRef>70</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="0" y="8.75"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<attributeRef>72</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="3">
			<position x="100" y="-100"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-7.5"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>19</objectRef>
				<objectRef>13</objectRef>
				<objectRef>55</objectRef>
				<objectRef>39</objectRef>
				<objectRef>40</objectRef>
				<objectRef>46</objectRef>
				<objectRef>49</objectRef>
				<objectRef>18</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="0" y="8.75"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<attributeRef>73</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="4">
			<position x="0" y="-100"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-7.5"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>10</objectRef>
				<objectRef>36</objectRef>
				<objectRef>37</objectRef>
				<objectRef>17</objectRef>
				<objectRef>57</objectRef>
				<objectRef>23</objectRef>
				<objectRef>60</objectRef>
				<objectRef>61</objectRef>
				<objectRef>6</objectRef>
				<objectRef>7</objectRef>
				<objectRef>27</objectRef>
				<objectRef>29</objectRef>
				<objectRef>30</objectRef>
				<objectRef>69</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="0" y="8.75"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<attributeRef>74</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="5">
			<position x="-50" y="-50"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-7.5"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>35</objectRef>
				<objectRef>20</objectRef>
				<objectRef>12</objectRef>
				<objectRef>51</objectRef>
				<objectRef>15</objectRef>
				<objectRef>38</objectRef>
				<objectRef>21</objectRef>
				<objectRef>56</objectRef>
				<objectRef>58</objectRef>
				<objectRef>22</objectRef>
				<objectRef>4</objectRef>
				<objectRef>41</objectRef>
				<objectRef>42</objectRef>
				<objectRef>44</objectRef>
				<objectRef>24</objectRef>
				<objectRef>26</objectRef>
				<objectRef>64</objectRef>
				<objectRef>66</objectRef>
				<objectRef>47</objectRef>
				<objectRef>32</objectRef>
				<objectRef>33</objectRef>
				<objectRef>34</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="0" y="8.75"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<attributeRef>75</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="6">
			<position x="-100" y="-100"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-7.5"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>52</objectRef>
				<objectRef>14</objectRef>
				<objectRef>25</objectRef>
				<objectRef>67</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="0" y="8.75"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<attributeRef>76</attributeRef>
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
	<diagram title="WinMarks (Graphics/Disk) für 486/66 PCs">
		<concept id="1">
			<position x="0" y="0"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-20"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="0" y="20"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<attributeRef>77</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="2">
			<position x="40" y="-40"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-20"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="20" y="20"/>
					<textAlignment>left</textAlignment>
				</labelStyle>
				<attributeRef>78</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="3">
			<position x="80" y="-80"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-20"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="20" y="20"/>
					<textAlignment>left</textAlignment>
				</labelStyle>
				<attributeRef>79</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="4">
			<position x="120" y="-120"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-20"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="20" y="20"/>
					<textAlignment>left</textAlignment>
				</labelStyle>
				<attributeRef>80</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="5">
			<position x="160" y="-160"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-20"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="20" y="20"/>
					<textAlignment>left</textAlignment>
				</labelStyle>
				<attributeRef>81</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="6">
			<position x="200" y="-200"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-20"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="20" y="20"/>
					<textAlignment>left</textAlignment>
				</labelStyle>
				<attributeRef>82</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="7">
			<position x="240" y="-240"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-20"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="20" y="20"/>
					<textAlignment>left</textAlignment>
				</labelStyle>
				<attributeRef>83</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="8">
			<position x="-40" y="-40"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-20"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>38</objectRef>
				<objectRef>65</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="-20" y="20"/>
					<textAlignment>right</textAlignment>
				</labelStyle>
				<attributeRef>80</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="9">
			<position x="-80" y="-80"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-20"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>13</objectRef>
				<objectRef>52</objectRef>
				<objectRef>71</objectRef>
				<objectRef>72</objectRef>
				<objectRef>41</objectRef>
				<objectRef>62</objectRef>
				<objectRef>6</objectRef>
				<objectRef>69</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="-20" y="20"/>
					<textAlignment>right</textAlignment>
				</labelStyle>
				<attributeRef>82</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="10">
			<position x="-120" y="-120"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-20"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>3</objectRef>
				<objectRef>42</objectRef>
				<objectRef>45</objectRef>
				<objectRef>7</objectRef>
				<objectRef>8</objectRef>
				<objectRef>28</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="-20" y="20"/>
					<textAlignment>right</textAlignment>
				</labelStyle>
				<attributeRef>84</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="11">
			<position x="-160" y="-160"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-20"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="-20" y="20"/>
					<textAlignment>right</textAlignment>
				</labelStyle>
				<attributeRef>85</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="12">
			<position x="-200" y="-200"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-20"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="-20" y="20"/>
					<textAlignment>right</textAlignment>
				</labelStyle>
				<attributeRef>86</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="13">
			<position x="-240" y="-240"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-20"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="-20" y="20"/>
					<textAlignment>right</textAlignment>
				</labelStyle>
				<attributeRef>87</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="14">
			<position x="0" y="-80"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-20"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>36</objectRef>
				<objectRef>55</objectRef>
				<objectRef>21</objectRef>
				<objectRef>63</objectRef>
				<objectRef>32</objectRef>
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
				<objectRef>19</objectRef>
				<objectRef>20</objectRef>
				<objectRef>51</objectRef>
				<objectRef>53</objectRef>
				<objectRef>37</objectRef>
				<objectRef>54</objectRef>
				<objectRef>39</objectRef>
				<objectRef>57</objectRef>
				<objectRef>22</objectRef>
				<objectRef>5</objectRef>
				<objectRef>2</objectRef>
				<objectRef>44</objectRef>
				<objectRef>24</objectRef>
				<objectRef>73</objectRef>
				<objectRef>64</objectRef>
				<objectRef>29</objectRef>
				<objectRef>30</objectRef>
				<objectRef>67</objectRef>
				<objectRef>68</objectRef>
				<objectRef>31</objectRef>
				<objectRef>34</objectRef>
				<objectRef>49</objectRef>
				<objectRef>18</objectRef>
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
				<objectRef>16</objectRef>
				<objectRef>17</objectRef>
				<objectRef>56</objectRef>
				<objectRef>58</objectRef>
				<objectRef>23</objectRef>
				<objectRef>61</objectRef>
				<objectRef>46</objectRef>
				<objectRef>27</objectRef>
				<objectRef>47</objectRef>
				<objectRef>48</objectRef>
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
				<objectRef>4</objectRef>
				<objectRef>25</objectRef>
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
				<objectRef>1</objectRef>
				<objectRef>66</objectRef>
				<objectRef>9</objectRef>
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
				<objectRef>15</objectRef>
				<objectRef>43</objectRef>
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
				<objectRef>60</objectRef>
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
				<objectRef>10</objectRef>
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
				<objectRef>11</objectRef>
				<objectRef>26</objectRef>
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
				<objectRef>40</objectRef>
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
				<objectRef>12</objectRef>
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
				<objectRef>50</objectRef>
				<objectRef>59</objectRef>
				<objectRef>33</objectRef>
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
				<objectRef>14</objectRef>
				<objectRef>70</objectRef>
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
				<attributeRef>88</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="3">
			<position x="-140" y="-140"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-21"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>12</objectRef>
				<objectRef>51</objectRef>
				<objectRef>52</objectRef>
				<objectRef>53</objectRef>
				<objectRef>37</objectRef>
				<objectRef>55</objectRef>
				<objectRef>56</objectRef>
				<objectRef>40</objectRef>
				<objectRef>57</objectRef>
				<objectRef>58</objectRef>
				<objectRef>59</objectRef>
				<objectRef>41</objectRef>
				<objectRef>61</objectRef>
				<objectRef>62</objectRef>
				<objectRef>42</objectRef>
				<objectRef>6</objectRef>
				<objectRef>25</objectRef>
				<objectRef>45</objectRef>
				<objectRef>26</objectRef>
				<objectRef>46</objectRef>
				<objectRef>64</objectRef>
				<objectRef>65</objectRef>
				<objectRef>66</objectRef>
				<objectRef>28</objectRef>
				<objectRef>29</objectRef>
				<objectRef>30</objectRef>
				<objectRef>31</objectRef>
				<objectRef>9</objectRef>
				<objectRef>32</objectRef>
				<objectRef>48</objectRef>
				<objectRef>34</objectRef>
				<objectRef>70</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="0" y="17.5"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<attributeRef>89</attributeRef>
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
				<attributeRef>90</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="5">
			<position x="140" y="-140"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-21"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>11</objectRef>
				<objectRef>13</objectRef>
				<objectRef>14</objectRef>
				<objectRef>15</objectRef>
				<objectRef>72</objectRef>
				<objectRef>54</objectRef>
				<objectRef>1</objectRef>
				<objectRef>21</objectRef>
				<objectRef>3</objectRef>
				<objectRef>23</objectRef>
				<objectRef>5</objectRef>
				<objectRef>2</objectRef>
				<objectRef>8</objectRef>
				<objectRef>49</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="0" y="17.5"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<attributeRef>91</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="6">
			<position x="0" y="-140"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-21"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>19</objectRef>
				<objectRef>35</objectRef>
				<objectRef>20</objectRef>
				<objectRef>10</objectRef>
				<objectRef>36</objectRef>
				<objectRef>50</objectRef>
				<objectRef>71</objectRef>
				<objectRef>16</objectRef>
				<objectRef>38</objectRef>
				<objectRef>17</objectRef>
				<objectRef>39</objectRef>
				<objectRef>22</objectRef>
				<objectRef>4</objectRef>
				<objectRef>60</objectRef>
				<objectRef>63</objectRef>
				<objectRef>43</objectRef>
				<objectRef>44</objectRef>
				<objectRef>24</objectRef>
				<objectRef>7</objectRef>
				<objectRef>73</objectRef>
				<objectRef>27</objectRef>
				<objectRef>47</objectRef>
				<objectRef>67</objectRef>
				<objectRef>68</objectRef>
				<objectRef>33</objectRef>
				<objectRef>69</objectRef>
				<objectRef>18</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="0" y="17.5"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<attributeRef>92</attributeRef>
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
	<diagram title="Festplattengrößen (ordinal)">
		<concept id="1">
			<position x="0" y="0"/>
			<objectContingent>
				<labelStyle>
					<offset x="15" y="-15"/>
					<textAlignment>left</textAlignment>
				</labelStyle>
				<objectRef>65</objectRef>
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
				<objectRef>19</objectRef>
				<objectRef>35</objectRef>
				<objectRef>20</objectRef>
				<objectRef>10</objectRef>
				<objectRef>11</objectRef>
				<objectRef>13</objectRef>
				<objectRef>36</objectRef>
				<objectRef>50</objectRef>
				<objectRef>51</objectRef>
				<objectRef>52</objectRef>
				<objectRef>53</objectRef>
				<objectRef>14</objectRef>
				<objectRef>71</objectRef>
				<objectRef>15</objectRef>
				<objectRef>72</objectRef>
				<objectRef>37</objectRef>
				<objectRef>54</objectRef>
				<objectRef>1</objectRef>
				<objectRef>16</objectRef>
				<objectRef>55</objectRef>
				<objectRef>38</objectRef>
				<objectRef>17</objectRef>
				<objectRef>21</objectRef>
				<objectRef>56</objectRef>
				<objectRef>39</objectRef>
				<objectRef>40</objectRef>
				<objectRef>57</objectRef>
				<objectRef>3</objectRef>
				<objectRef>58</objectRef>
				<objectRef>22</objectRef>
				<objectRef>59</objectRef>
				<objectRef>23</objectRef>
				<objectRef>41</objectRef>
				<objectRef>5</objectRef>
				<objectRef>60</objectRef>
				<objectRef>61</objectRef>
				<objectRef>63</objectRef>
				<objectRef>42</objectRef>
				<objectRef>43</objectRef>
				<objectRef>44</objectRef>
				<objectRef>24</objectRef>
				<objectRef>6</objectRef>
				<objectRef>25</objectRef>
				<objectRef>45</objectRef>
				<objectRef>26</objectRef>
				<objectRef>7</objectRef>
				<objectRef>73</objectRef>
				<objectRef>46</objectRef>
				<objectRef>64</objectRef>
				<objectRef>66</objectRef>
				<objectRef>28</objectRef>
				<objectRef>47</objectRef>
				<objectRef>29</objectRef>
				<objectRef>30</objectRef>
				<objectRef>67</objectRef>
				<objectRef>68</objectRef>
				<objectRef>31</objectRef>
				<objectRef>9</objectRef>
				<objectRef>32</objectRef>
				<objectRef>33</objectRef>
				<objectRef>69</objectRef>
				<objectRef>48</objectRef>
				<objectRef>34</objectRef>
				<objectRef>49</objectRef>
				<objectRef>18</objectRef>
				<objectRef>70</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="15" y="15"/>
					<textAlignment>left</textAlignment>
				</labelStyle>
				<attributeRef>93</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="3">
			<position x="0" y="-200"/>
			<objectContingent>
				<labelStyle>
					<offset x="15" y="-15"/>
					<textAlignment>left</textAlignment>
				</labelStyle>
				<objectRef>12</objectRef>
				<objectRef>4</objectRef>
				<objectRef>2</objectRef>
				<objectRef>62</objectRef>
				<objectRef>8</objectRef>
				<objectRef>27</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="15" y="15"/>
					<textAlignment>left</textAlignment>
				</labelStyle>
				<attributeRef>28</attributeRef>
			</attributeContingent>
		</concept>
		<edge from="1" to="2"/>
		<edge from="2" to="3"/>
	</diagram>
	<diagram title="DOS-Win-Mark">
		<concept id="1">
			<position x="0" y="0"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-20"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="0" y="20"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<attributeRef>94</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="2">
			<position x="40" y="-40"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-20"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="20" y="20"/>
					<textAlignment>left</textAlignment>
				</labelStyle>
				<attributeRef>95</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="3">
			<position x="80" y="-80"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-20"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="20" y="20"/>
					<textAlignment>left</textAlignment>
				</labelStyle>
				<attributeRef>96</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="4">
			<position x="120" y="-120"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-20"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="20" y="20"/>
					<textAlignment>left</textAlignment>
				</labelStyle>
				<attributeRef>97</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="5">
			<position x="160" y="-160"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-20"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="20" y="20"/>
					<textAlignment>left</textAlignment>
				</labelStyle>
				<attributeRef>98</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="6">
			<position x="200" y="-200"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-20"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="20" y="20"/>
					<textAlignment>left</textAlignment>
				</labelStyle>
				<attributeRef>99</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="7">
			<position x="240" y="-240"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-20"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="20" y="20"/>
					<textAlignment>left</textAlignment>
				</labelStyle>
				<attributeRef>100</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="8">
			<position x="-40" y="-40"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-20"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>55</objectRef>
				<objectRef>65</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="-20" y="20"/>
					<textAlignment>right</textAlignment>
				</labelStyle>
				<attributeRef>80</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="9">
			<position x="-80" y="-80"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-20"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>19</objectRef>
				<objectRef>20</objectRef>
				<objectRef>10</objectRef>
				<objectRef>13</objectRef>
				<objectRef>51</objectRef>
				<objectRef>52</objectRef>
				<objectRef>53</objectRef>
				<objectRef>71</objectRef>
				<objectRef>72</objectRef>
				<objectRef>37</objectRef>
				<objectRef>54</objectRef>
				<objectRef>1</objectRef>
				<objectRef>39</objectRef>
				<objectRef>57</objectRef>
				<objectRef>22</objectRef>
				<objectRef>41</objectRef>
				<objectRef>5</objectRef>
				<objectRef>60</objectRef>
				<objectRef>43</objectRef>
				<objectRef>6</objectRef>
				<objectRef>64</objectRef>
				<objectRef>66</objectRef>
				<objectRef>29</objectRef>
				<objectRef>30</objectRef>
				<objectRef>67</objectRef>
				<objectRef>68</objectRef>
				<objectRef>31</objectRef>
				<objectRef>9</objectRef>
				<objectRef>69</objectRef>
				<objectRef>34</objectRef>
				<objectRef>49</objectRef>
				<objectRef>18</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="-20" y="20"/>
					<textAlignment>right</textAlignment>
				</labelStyle>
				<attributeRef>82</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="10">
			<position x="-120" y="-120"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-20"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>11</objectRef>
				<objectRef>12</objectRef>
				<objectRef>50</objectRef>
				<objectRef>16</objectRef>
				<objectRef>56</objectRef>
				<objectRef>3</objectRef>
				<objectRef>59</objectRef>
				<objectRef>23</objectRef>
				<objectRef>42</objectRef>
				<objectRef>45</objectRef>
				<objectRef>7</objectRef>
				<objectRef>8</objectRef>
				<objectRef>46</objectRef>
				<objectRef>27</objectRef>
				<objectRef>33</objectRef>
				<objectRef>48</objectRef>
				<objectRef>70</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="-20" y="20"/>
					<textAlignment>right</textAlignment>
				</labelStyle>
				<attributeRef>84</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="11">
			<position x="-160" y="-160"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-20"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="-20" y="20"/>
					<textAlignment>right</textAlignment>
				</labelStyle>
				<attributeRef>85</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="12">
			<position x="-200" y="-200"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-20"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="-20" y="20"/>
					<textAlignment>right</textAlignment>
				</labelStyle>
				<attributeRef>86</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="13">
			<position x="-240" y="-240"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-20"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="-20" y="20"/>
					<textAlignment>right</textAlignment>
				</labelStyle>
				<attributeRef>87</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="14">
			<position x="0" y="-80"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-20"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
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
				<objectRef>36</objectRef>
				<objectRef>21</objectRef>
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
				<objectRef>38</objectRef>
				<objectRef>63</objectRef>
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
				<objectRef>32</objectRef>
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
				<objectRef>15</objectRef>
				<objectRef>62</objectRef>
				<objectRef>24</objectRef>
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
				<objectRef>14</objectRef>
				<objectRef>17</objectRef>
				<objectRef>58</objectRef>
				<objectRef>61</objectRef>
				<objectRef>28</objectRef>
				<objectRef>47</objectRef>
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
				<objectRef>44</objectRef>
				<objectRef>73</objectRef>
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
				<objectRef>2</objectRef>
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
				<objectRef>26</objectRef>
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
				<objectRef>35</objectRef>
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
				<objectRef>4</objectRef>
				<objectRef>25</objectRef>
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
				<objectRef>40</objectRef>
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
	<diagram title="Ports auf dem Motherboard">
		<concept id="1">
			<position x="-30" y="0"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-9"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>35</objectRef>
				<objectRef>20</objectRef>
				<objectRef>10</objectRef>
				<objectRef>36</objectRef>
				<objectRef>51</objectRef>
				<objectRef>53</objectRef>
				<objectRef>71</objectRef>
				<objectRef>15</objectRef>
				<objectRef>72</objectRef>
				<objectRef>37</objectRef>
				<objectRef>54</objectRef>
				<objectRef>16</objectRef>
				<objectRef>38</objectRef>
				<objectRef>21</objectRef>
				<objectRef>56</objectRef>
				<objectRef>40</objectRef>
				<objectRef>57</objectRef>
				<objectRef>58</objectRef>
				<objectRef>22</objectRef>
				<objectRef>4</objectRef>
				<objectRef>23</objectRef>
				<objectRef>41</objectRef>
				<objectRef>62</objectRef>
				<objectRef>42</objectRef>
				<objectRef>44</objectRef>
				<objectRef>24</objectRef>
				<objectRef>45</objectRef>
				<objectRef>26</objectRef>
				<objectRef>73</objectRef>
				<objectRef>27</objectRef>
				<objectRef>47</objectRef>
				<objectRef>29</objectRef>
				<objectRef>30</objectRef>
				<objectRef>67</objectRef>
				<objectRef>68</objectRef>
				<objectRef>31</objectRef>
				<objectRef>32</objectRef>
				<objectRef>48</objectRef>
				<objectRef>34</objectRef>
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
				<objectRef>25</objectRef>
				<objectRef>28</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="-6.300000000000001" y="9"/>
					<textAlignment>right</textAlignment>
				</labelStyle>
				<attributeRef>101</attributeRef>
				<attributeRef>102</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="3">
			<position x="-30" y="-120"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-9"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>7</objectRef>
				<objectRef>18</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="0" y="10.5"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<attributeRef>103</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="4">
			<position x="30" y="-120"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-9"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>11</objectRef>
				<objectRef>52</objectRef>
				<objectRef>14</objectRef>
				<objectRef>55</objectRef>
				<objectRef>3</objectRef>
				<objectRef>61</objectRef>
				<objectRef>63</objectRef>
				<objectRef>43</objectRef>
				<objectRef>6</objectRef>
				<objectRef>8</objectRef>
				<objectRef>46</objectRef>
				<objectRef>64</objectRef>
				<objectRef>65</objectRef>
				<objectRef>66</objectRef>
				<objectRef>9</objectRef>
				<objectRef>33</objectRef>
				<objectRef>70</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="4.800000000000001" y="8.100000000000001"/>
					<textAlignment>left</textAlignment>
				</labelStyle>
				<attributeRef>104</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="5">
			<position x="30" y="-180"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-9"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>1</objectRef>
				<objectRef>39</objectRef>
				<objectRef>5</objectRef>
				<objectRef>60</objectRef>
				<objectRef>2</objectRef>
				<objectRef>69</objectRef>
				<objectRef>49</objectRef>
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
				<objectRef>19</objectRef>
				<objectRef>12</objectRef>
				<objectRef>50</objectRef>
				<objectRef>59</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="-3.5999999999999996" y="9.600000000000001"/>
					<textAlignment>right</textAlignment>
				</labelStyle>
				<attributeRef>105</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="7">
			<position x="-90" y="-180"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-9"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>13</objectRef>
				<objectRef>17</objectRef>
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
				<objectRef>35</objectRef>
				<objectRef>36</objectRef>
				<objectRef>5</objectRef>
				<objectRef>2</objectRef>
				<objectRef>45</objectRef>
				<objectRef>65</objectRef>
				<objectRef>28</objectRef>
				<objectRef>30</objectRef>
				<objectRef>68</objectRef>
				<objectRef>48</objectRef>
				<objectRef>49</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="-11.6" y="13.6"/>
					<textAlignment>right</textAlignment>
				</labelStyle>
				<attributeRef>57</attributeRef>
				<attributeRef>58</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="2">
			<position x="-40" y="-40"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-12"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>19</objectRef>
				<objectRef>10</objectRef>
				<objectRef>14</objectRef>
				<objectRef>25</objectRef>
				<objectRef>46</objectRef>
				<objectRef>29</objectRef>
				<objectRef>34</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="-14" y="12"/>
					<textAlignment>right</textAlignment>
				</labelStyle>
				<attributeRef>106</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="3">
			<position x="-80" y="-80"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-12"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>11</objectRef>
				<objectRef>13</objectRef>
				<objectRef>53</objectRef>
				<objectRef>21</objectRef>
				<objectRef>57</objectRef>
				<objectRef>3</objectRef>
				<objectRef>58</objectRef>
				<objectRef>22</objectRef>
				<objectRef>59</objectRef>
				<objectRef>63</objectRef>
				<objectRef>8</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="-10.4" y="14"/>
					<textAlignment>right</textAlignment>
				</labelStyle>
				<attributeRef>107</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="4">
			<position x="-120" y="-120"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-12"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>4</objectRef>
				<objectRef>18</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="-11.2" y="13.6"/>
					<textAlignment>right</textAlignment>
				</labelStyle>
				<attributeRef>108</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="5">
			<position x="40" y="-40"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-12"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>52</objectRef>
				<objectRef>1</objectRef>
				<objectRef>55</objectRef>
				<objectRef>17</objectRef>
				<objectRef>23</objectRef>
				<objectRef>62</objectRef>
				<objectRef>42</objectRef>
				<objectRef>6</objectRef>
				<objectRef>73</objectRef>
				<objectRef>27</objectRef>
				<objectRef>47</objectRef>
				<objectRef>67</objectRef>
				<objectRef>31</objectRef>
				<objectRef>9</objectRef>
				<objectRef>32</objectRef>
				<objectRef>69</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="12" y="12"/>
					<textAlignment>left</textAlignment>
				</labelStyle>
				<attributeRef>109</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="6">
			<position x="0" y="-80"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-12"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>54</objectRef>
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
				<objectRef>51</objectRef>
				<objectRef>70</objectRef>
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
				<objectRef>50</objectRef>
				<objectRef>71</objectRef>
				<objectRef>72</objectRef>
				<objectRef>38</objectRef>
				<objectRef>56</objectRef>
				<objectRef>39</objectRef>
				<objectRef>40</objectRef>
				<objectRef>60</objectRef>
				<objectRef>61</objectRef>
				<objectRef>43</objectRef>
				<objectRef>7</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="12" y="12"/>
					<textAlignment>left</textAlignment>
				</labelStyle>
				<attributeRef>110</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="10">
			<position x="40" y="-120"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-12"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
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
				<objectRef>12</objectRef>
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
				<objectRef>66</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="12" y="12"/>
					<textAlignment>left</textAlignment>
				</labelStyle>
				<attributeRef>111</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="14">
			<position x="80" y="-160"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-12"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>41</objectRef>
				<objectRef>24</objectRef>
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
				<objectRef>44</objectRef>
				<objectRef>26</objectRef>
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
				<objectRef>20</objectRef>
				<objectRef>15</objectRef>
				<objectRef>33</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="-12" y="13.2"/>
					<textAlignment>right</textAlignment>
				</labelStyle>
				<attributeRef>112</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="18">
			<position x="-120" y="-200"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-12"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
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
				<objectRef>37</objectRef>
				<objectRef>16</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="12" y="12"/>
					<textAlignment>left</textAlignment>
				</labelStyle>
				<attributeRef>113</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="22">
			<position x="120" y="-200"/>
			<objectContingent>
				<labelStyle>
					<offset x="0" y="-12"/>
					<textAlignment>center</textAlignment>
				</labelStyle>
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
				<objectRef>64</objectRef>
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
