This is a simple implementation of a JFreeChart based database viewer.
It can be used as plugin for ToscanaJ by copying the files from the
archive into ToscanaJ's plugin directory.

At the moment only a very basic syntax is supported, most JFreeChart
features are not mapped into XML. Here is how an example looks like:

<objectListView name="Benchmark Chart..." class="net.sourceforge.toscanaj.dbviewer.JFreeChartViewer">
  <template>
    <type>HorizontalBarchart</type>
    <title>Benchmarks</title>
    <font family="Tahoma" size="12"/>
    <bgcolor>#fff098</bgcolor>
    <domain>PCName</domain>
    <range>dosmark</range>
    <range>diskmark</range>
    <range>graphics</range>
  </template>
</objectListView>

This example should work in the standard PCTest example coming with
ToscanaJ. The formatting entries are optional and should do what you
would expect. The other elements are:

<type>
  can be one of: HorizontalBarchart, VerticalBarchart, 
                 HorizontalLinechart, VerticalLinechart
<domain>
  specifies the SQL column to find the domain objects for the chart.
  This is typically the same as the key in ToscanaJ systems
  
<range>
  gives the values to look at. If multiple <range>s are given each
  will create a bar or line in the chart