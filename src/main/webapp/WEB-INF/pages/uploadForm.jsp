<!DOCTYPE html>
<html>
<head>
<style>
.btnCls {
  background-color: #4CAF50; /* Green */
  border: none;
  color: white;
  padding: 15px 32px;
  text-align: center;
  text-decoration: none;
  display: inline-block;
  font-size: 16px;
}
</style>
<meta charset="UTF-8">
<script type="text/javascript" src="resources/jquery-3.3.1.min.js"></script>
<script type="text/javascript" src="resources/action.js"></script>
<script>
$(document).ready(function(){
  $(".typeSelector").change(function(){
        $("#excelType").val(null);
        $("#xmlType").val(null);
		if(this.value == "TYPE1"){
			$('.sourceSelector').prop('selectedIndex',0);
			$("#type1Div").show();
			$("#xmlTypeDiv").hide();
			$("#type2Div").hide();
			$("#sbtDiv").hide();
		}else if(this.value == "TYPE2"){
			$("#type1Div").hide();
			$("#type2Div").show();
			$("#sbtDiv").show();
			loadExcelConversion();
		}else{
			$("#type1Div").hide();
			$("#xmlTypeDiv").hide();
			$("#type2Div").hide();
			$("#sbtDiv").hide();
		}
  });
  $(".sourceSelector").change(function(){
        $("#excelType").val(null);
        $("#xmlType").val(null);
		if(this.value == "FLAT_FILE"){
		    loadExcelConversion();
			$("#xmlTypeDiv").hide();
			$("#type2Div").show();
			$("#sbtDiv").show();
		}else if(this.value == "RELATIONAL"){
		    loadExcelConversion();
			$("#xmlTypeDiv").hide();
			$("#type2Div").show();
			$("#sbtDiv").show();
		}else if(this.value == "XML"){
		    loadExcelConversion();
		    loadXmlConversion();
			$("#xmlTypeDiv").show();
			$("#type2Div").show();
			$("#sbtDiv").show();
		}else{
			$("#xmlTypeDiv").hide();
			$("#type2Div").hide();
			$("#sbtDiv").hide();
		}
  });
});
</script>
<title>File Converter</title>
</head>
<body>
<center>
		<h2><font color="green"><u>Excel to XML converter</u></font></h2>
		<!-- Need to select Type first -->
		<div id="mainHead">
		<h3>Please select type:</h3>
		<select class="typeSelector">
			<option value="">-- Select Type --</option>
  			<option value="TYPE1">Type1</option>
  			<option value="TYPE2">Type2</option>
  		</select>
		</div>
		<!-- if Type1 need to show this -->
		<div id="type1Div"  style="display:none">
		<h3>Please select source type:</h3>
		<select class="sourceSelector">
			<option value="">-- Select Source Type --</option>
  			<option value="FLAT_FILE">Flat File</option>
  			<option value="XML">XML</option>
  			<option value="RELATIONAL">RELATIONAL</option>
  		</select>
			<div id="xmlTypeDiv" style="display:none">
                <h3>Upload XML file:</h3>
                <input type="file" name="xmlType" id="xmlType"/>
			</div>
		</div>
		<!-- if Type2 need to show this -->
		<div id="type2Div" style="display:none">
            <h3>Upload excel file:</h3>
            <input type="file" name="excelType" id="excelType"/>
		</div>
		<br/>
		<div id="sbtDiv" style="display:none">
			<button type="button" class="btnCls" id="sbtBtn" onclick="uploadFile()">Upload</button>
		</div>
</body>

</html>