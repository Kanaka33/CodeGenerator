var excelFileByteArray = [];
var xmlFileByteArray = [];
function uploadFile(){
    var typeSelectorVal = $(".typeSelector").val();
    if (typeof typeSelectorVal !== 'undefined'){
        var uploadForm = {};
        uploadForm.typeEnum = typeSelectorVal;
        if(typeSelectorVal == "TYPE1"){
            var sourceSelector = $(".sourceSelector").val();
            if (typeof sourceSelector !== 'undefined'){
                uploadForm.sourceType = sourceSelector;
                $("#excelType")[0].files.length
                if(sourceSelector == "FLAT_FILE"){
                   if(validateExcelFile(uploadForm)){
                        saveUploadForm(uploadForm);
                   }else{
                        alert("Please upload valid excel file");
                   }
                }else if(sourceSelector == "RELATIONAL"){
                   if(validateExcelFile(uploadForm)){
                        saveUploadForm(uploadForm);
                   }else{
                        alert("Please upload valid excel file");
                   }
                }else if(sourceSelector == "XML"){
                    if(validateExcelFile(uploadForm) && validateXmlFile(uploadForm)){
                        saveUploadForm(uploadForm);
                   }else{
                        alert("Please upload valid excel & xml file");
                   }
                }else{
                    alert("Invalid Source Type.");
                }
            }

        }else if(typeSelectorVal == "TYPE2"){
            if(validateExcelFile(uploadForm)){
                saveUploadForm(uploadForm);
            }else{
                alert("Please upload valid excel file");
            }
        }else{
            alert("Please refresh page.");
        }
    }

}
function validateExcelFile(uploadForm){
    var excelFileCount = $("#excelType")[0].files.length;
    if(excelFileCount == 0){
        return false;
    }else{
        var fileType = $("#excelType")[0].files[0].type;
        if(fileType.indexOf("excel") != -1 || fileType.indexOf("sheet") != -1){
            var fileInfo = $("#excelType")[0].files[0];
            uploadForm.excelFileName = $("#excelType")[0].files[0].name;
            uploadForm.excelFile = excelFileByteArray;
        }else{
            return false;
        }
    }
    return true;
}

function validateXmlFile(uploadForm){
    var xmlFileCount = $("#xmlType")[0].files.length;
    if(xmlFileCount == 0){
        return false;
    }else{
        var fileType = $("#xmlType")[0].files[0].type;
        var fileName = $("#xmlType")[0].files[0].name;
        if(fileType.indexOf("xml") != -1 && fileName.endsWith(".xsd")){
            var fileInfo = $("#xmlType")[0].files[0];
            uploadForm.xmlFileName = $("#xmlType")[0].files[0].name;
            uploadForm.xmlFile = xmlFileByteArray;
        }else if(fileType.indexOf("xml") != -1 && fileName.endsWith(".XML")){
            var fileInfo = $("#xmlType")[0].files[0];
            uploadForm.xmlFileName = $("#xmlType")[0].files[0].name;
            uploadForm.xmlFile = xmlFileByteArray;
        }else{
            return false;
        }
    }
    return true;
}

function saveUploadForm(uploadForm){
    $.ajax({
          type: "POST",
          contentType: "application/json;charset=utf-8",
          url: "uploadFiles.htm",
          data: JSON.stringify(uploadForm),
          success: function(response){
        	 $("html").html(response);
          }
    });
}
function loadExcelConversion(){
    document.querySelector("#excelType").addEventListener('change', function() {
        var reader = new FileReader();
        excelFileByteArray = [];
        reader.readAsArrayBuffer(this.files[0]);
        reader.onloadend  = function(evt) {
            if (evt.target.readyState == FileReader.DONE) {
                 var arrayBuffer = evt.target.result,
                 array = new Uint8Array(arrayBuffer);
                 for (var i = 0; i < array.length; i++) {
                    excelFileByteArray.push(array[i]);
                 }
            }
        }
    }, false);
}

function loadXmlConversion(){
    document.querySelector("#xmlType").addEventListener('change', function() {
        var reader = new FileReader();
        xmlFileByteArray = [];
        reader.readAsArrayBuffer(this.files[0]);
        reader.onloadend  = function(evt) {
            if (evt.target.readyState == FileReader.DONE) {
                 var arrayBuffer = evt.target.result,
                 array = new Uint8Array(arrayBuffer);
                 for (var i = 0; i < array.length; i++) {
                    xmlFileByteArray.push(array[i]);
                 }
            }
        }
    }, false);
}