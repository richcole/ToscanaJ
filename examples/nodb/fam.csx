<?xml version="1.0" encoding="UTF-8"?>
<conceptualSchema version="TJ0.4">
	<context>
		<object id="viola">Viola</object>
		<object id="sarah">Sarah</object>
		<object id="nora">Nora</object>
		<object id="elin">Elin</object>
		<object id="bernhard">Bernhard</object>
		<attribute id="1" name="männlich"/>
		<attribute id="2" name="weiblich"/>
		<attribute id="3" name="beruf"/>
		<attribute id="4" name="student"/>
		<attribute id="5" name="schüler"/>
	</context>
	<diagram title="Geschlecht">
		<concept id="1">
			<position x="0" y="0" />
			<objectContingent />
			<attributeContingent />
		</concept>
		<concept id="2">
			<position x="60" y="-60" />
			<objectContingent>
				<labelStyle>
					<textColor>#0000ff</textColor>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>bernhard</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<offset x="6" y="6" />
					<bgColor>#ccccff</bgColor>
				</labelStyle>
				<attributeRef>1</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="3">
			<position x="-60" y="-60" />
			<objectContingent>
				<labelStyle>
					<textColor>#0000ff</textColor>
					<textAlignment>center</textAlignment>
				</labelStyle>
				<objectRef>viola</objectRef>
				<objectRef>sarah</objectRef>
				<objectRef>nora</objectRef>
				<objectRef>elin</objectRef>
			</objectContingent>
			<attributeContingent>
				<labelStyle>
					<bgColor>#ffcccc</bgColor>
				</labelStyle>
				<attributeRef>2</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="4">
			<position x="0" y="-120" />
			<objectContingent />
			<attributeContingent />
		</concept>
		<edge from="1" to="2" />
		<edge from="1" to="3" />
		<edge from="2" to="4" />
		<edge from="3" to="4" />
	</diagram>
	<diagram title="Status">
		<concept id="1">
			<position x="0" y="0" />
			<objectContingent />
			<attributeContingent />
		</concept>
		<concept id="2">
			<position x="120" y="-120" />
			<objectContingent>
				<objectRef>elin</objectRef>
				<objectRef>bernhard</objectRef>
			</objectContingent>
			<attributeContingent>
				<attributeRef>3</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="3">
			<position x="0" y="-120" />
			<objectContingent>
				<objectRef>sarah</objectRef>
				<objectRef>nora</objectRef>
			</objectContingent>
			<attributeContingent>
				<attributeRef>4</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="4">
			<position x="-120" y="-120" />
			<objectContingent>
				<objectRef>viola</objectRef>
			</objectContingent>
			<attributeContingent>
				<attributeRef>5</attributeRef>
			</attributeContingent>
		</concept>
		<concept id="5">
			<position x="0" y="-240" />
			<objectContingent />
			<attributeContingent />
		</concept>
		<edge from="1" to="2" />
		<edge from="1" to="3" />
		<edge from="1" to="4" />
		<edge from="2" to="5" />
		<edge from="3" to="5" />
		<edge from="4" to="5" />
	</diagram>
</conceptualSchema>
