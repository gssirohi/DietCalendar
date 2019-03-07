package com.supercilex.poiandroid.demo

import android.Manifest
import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import org.apache.poi.ss.usermodel.*
import org.apache.poi.ss.usermodel.charts.AxisCrosses
import org.apache.poi.ss.usermodel.charts.AxisPosition
import org.apache.poi.ss.usermodel.charts.DataSources
import org.apache.poi.ss.usermodel.charts.LegendPosition
import org.apache.poi.ss.util.CellRangeAddress
import org.apache.poi.xssf.usermodel.*
import org.json.JSONArray
import org.json.JSONObject
import org.openxmlformats.schemas.drawingml.x2006.chart.CTTitle
import pub.devrel.easypermissions.EasyPermissions

import java.io.*
import android.media.MediaScannerConnection
import android.os.Environment
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.google.gson.Gson
import com.techticz.app.base.BaseDIActivity
import com.techticz.app.model.recipe.Recipe
import com.techticz.app.repo.DeveloperRepository
import com.techticz.app.repo.RecipeRepository
import com.techticz.dietcalendar.R
import com.techticz.networking.model.Status
import kotlinx.android.synthetic.main.content_tool.*
import javax.inject.Inject


class ToolActivity : BaseDIActivity(), View.OnClickListener {

    @Inject
    lateinit var developerRepo: DeveloperRepository

    @Inject
    lateinit var recipeRepo: RecipeRepository

    val FOOD_LAST_COLUMN_INDEX = ((26 * 3) + 7) - 1
    val FOOD_HEDAR_LAST_ROW_INDEX = 5 - 1
    val FOOD_UNIT_ROW_INDEX = 5
    val FOOD_START_ROW_INDEX = 6
    var FOOD_MAX_ROW_INDEX = 380-1//10-1 //15 // 375
    val FOOD_SHEET_NAME = "food"

    val RECIPE_LAST_COLUMN_INDEX = ((26 * 1) + 2) - 1
    val RECIPE_HEDAR_LAST_ROW_INDEX = 3 - 1
    val RECIPE_UNIT_ROW_INDEX = 4-1
    val RECIPE_START_ROW_INDEX = 5-1
    var RECIPE_MAX_ROW_INDEX = 78-1/*357 - 1*/
    val RECIPE_SHEET_NAME = "recipe"

    val PLATE_LAST_COLUMN_INDEX = ((26 * 1) ) - 1
    val PLATE_HEDAR_LAST_ROW_INDEX = 3 - 1
    val PLATE_UNIT_ROW_INDEX = 4-1
    val PLATE_START_ROW_INDEX = 5-1
    var PLATE_MAX_ROW_INDEX = 54-1/*357 - 1*/
    val PLATE_SHEET_NAME = "plate"

    val PLAN_LAST_COLUMN_INDEX = ((26 * 2) + 6) - 1
    val PLAN_HEDAR_LAST_ROW_INDEX = 3 - 1
    val PLAN_UNIT_ROW_INDEX = 4-1
    val PLAN_START_ROW_INDEX = 5-1
    var PLAN_MAX_ROW_INDEX = 11-1/*357 - 1*/
    val PLAN_SHEET_NAME = "dietplan"

    override fun onClick(v: View?) {
        showProgressDialog()
        async {
            var workbook = readExcelFile()
            workbook?.let {
                when(v?.id){
                    R.id.bt_export_all->createAllDocumentsJsonFile(workbook)
                    R.id.bt_export_plan->createPlanJson(workbook)
                    R.id.bt_export_plate->createPlateJson(workbook)
                    R.id.bt_export_recipe->createRecipeJson(workbook)
                    R.id.bt_export_food->createFoodJson(workbook)
                }
            }

        }.addOnSuccessListener {
            Toast.makeText(this, "Excel is converted to json", Toast.LENGTH_LONG).show()
            hideProgressDialog()
        }.addOnFailureListener {
            Log.e("SpreadsheetExporter", "Export failed", it)
            Toast.makeText(this, it.stackTrace.toList().toString(), Toast.LENGTH_LONG).show()
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tool)
        setSupportActionBar(findViewById(R.id.toolbar))
        if (!EasyPermissions.hasPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            EasyPermissions.requestPermissions(
                    this,
                    "Needed to write file",
                    2,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            return
        }

        findViewById<View>(R.id.bt_export_all).setOnClickListener(this)
        findViewById<View>(R.id.bt_export_plan).setOnClickListener(this)
        findViewById<View>(R.id.bt_export_plate).setOnClickListener(this)
        findViewById<View>(R.id.bt_export_recipe).setOnClickListener(this)
        findViewById<View>(R.id.bt_export_food).setOnClickListener(this)
        findViewById<View>(R.id.bt_export_db_data).setOnClickListener {
            showProgressDialog()
            recipeRepo?.fetchMyRecipes()?.observe(this, Observer{res->
                when(res.status){
                    Status.SUCCESS->{
                        startExportRecipe(res?.data?.recipes)
                    }
                }
            })

        }

        tiv_tool_plan?.editText?.setText(""+(PLAN_MAX_ROW_INDEX+1))
        tiv_tool_plate?.editText?.setText(""+(PLATE_MAX_ROW_INDEX+1))
        tiv_tool_recipe?.editText?.setText(""+(RECIPE_MAX_ROW_INDEX+1))
        tiv_tool_food?.editText?.setText(""+(FOOD_MAX_ROW_INDEX+1))

    }

    private fun startExportRecipe(recipes: List<Recipe>?) {
        async {
            var workbook = XSSFWorkbook()
            exportDBRecipeData(workbook,recipes!!)
            writeExcelFile("MyDietCalendar",workbook)
        }.addOnSuccessListener {
            Toast.makeText(this, "Excel is converted to json", Toast.LENGTH_LONG).show()
            hideProgressDialog()
        }.addOnFailureListener {
            Log.e("SpreadsheetExporter", "Export failed", it)
            Toast.makeText(this, it.stackTrace.toList().toString(), Toast.LENGTH_LONG).show()
        }
    }

    fun writeExcelFile( filename:String,workbook: XSSFWorkbook) {
        var f:File =  File(mPath, filename + ".xlsx");
        f.getParentFile().mkdirs();
        try  {
            val out = FileOutputStream(f)
            workbook.write(out)
            out.close()
        } catch (e:Exception) {
            e.printStackTrace();
            return;
        }
        //Toast.makeText(this,"Wrote " + f.getAbsolutePath(),Toast.LENGTH_SHORT).show();
        scanFile(this,f,"Text")
    }

    private var mProgressDialog: ProgressDialog? = null

    fun showProgressDialog() {
        mProgressDialog = ProgressDialog.show(this,"Working","Compiling exel data..",true)
    }

    fun hideProgressDialog() {
        mProgressDialog?.dismiss()
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


    class TreeNode<T>(value:T){
        var value:T = value
        var parent:TreeNode<T>? = null
        var children:MutableList<TreeNode<T>> = mutableListOf()

        fun addChild(node:TreeNode<T>){
            children.add(node)
            node.parent = this
        }
        override fun toString(): String {
            var s = "${value.toString()}"
            if (!children.isEmpty()) {
                s += " {" + children.map { it.toString() } + " }"
            }
            return s
        }

        fun traverse(obj: JSONObject, row: XSSFRow, unitRow:XSSFRow) {
            Log.i("TREE","Traversing "+this.toString())
            var it = children.iterator();
            var childJson = JSONObject()
            var childJsonPresent = false
            while (it.hasNext()){
                childJsonPresent = true
                it.next().traverse(childJson, row,unitRow)
            }

            try {
                if (childJson.length() == 0 && !childJsonPresent) {
                    var cell = row.getCell((this.value as Header).column)
                    if (cell == null) {
                        Log.e("SHEET", "Cell is NULL at row:" + row.rowNum + " column:" + (this.value as Header).column)
                        return
                    }
                    var label = getLabel(cell)
                    var unitCell = unitRow.getCell((this.value as Header).column)
                    var unitLabel = getLabel(unitCell)

                    if (label.equals("") && !unitLabel.equals("String", true)) {
                        Log.e("SHEET", "Cell is EMPTY at row:" + row.rowNum + " column:" + (this.value as Header).column)
                        obj.put(this.value.toString(), null)
                    } else {
                        if (unitLabel.equals("Integer", true)) {
                            label = label.replace(".0","")
                            obj.put(this.value.toString(), label.toInt())
                        } else if (unitLabel.equals("long", true)) {
                            obj.put(this.value.toString(), label.toFloat())
                        } else if (unitLabel.equals("boolean", true)) {
                            obj.put(this.value.toString(), label.toBoolean())
                        } else if (unitLabel.equals("jsonObject", true)) {
                            var obj = JSONObject(label)
                            obj.put(this.value.toString(), obj)

                        } else if (unitLabel.equals("jsonArray", true)) {
                            var array = JSONArray()
                            if(!label.startsWith("[")){
                                array = getFromCommaSeparetedArray(label)
                            } else {
                                array = JSONArray(label)
                            }
                            obj.put(this.value.toString(), array)

                        } else {//String
                            obj.put(this.value.toString(), label)
                        }
                    }


                } else {
                    obj.put(this.value.toString(), childJson)
                }
            } catch (e:Exception){
                Log.e("ERROR","EXCEPTION at row:" + row.rowNum + " column:" + (this.value as Header).column)
                e.printStackTrace()
            }
        }

        private fun getFromCommaSeparetedArray(label: String): JSONArray {
            var array  = JSONArray()
            if(!TextUtils.isEmpty(label)){
                var commaArrayString = label.replace("\n","")
                var commaArray = commaArrayString.split(".");
                for(item in commaArray){
                    array.put(item)
                }
            }
            return array
        }

        private fun getLabel(thisCell: XSSFCell): String {
            var value:String
            if (!TextUtils.isEmpty(thisCell.toString()) && (thisCell.toString().startsWith("LEFT")
                    || thisCell.toString().startsWith("RIGHT")
                    || thisCell.toString().startsWith("IFERROR")
                    || thisCell.toString().startsWith("f!")
                    || thisCell.toString().startsWith("food!")
                    || thisCell.toString().startsWith("recipe!")
                    || thisCell.toString().startsWith("plate!")
                            || thisCell.toString().startsWith("CONCATENATE"))) {
                value = thisCell.rawValue
            } else {
                value = thisCell.toString()
            }
            return value
        }
    }

    class Header(val label:String,val row: Int,val column:Int,val range:Int){
        override fun toString(): String {
            return label
        }
    }


    private fun createAllDocumentsJsonFile(myWorkBook: XSSFWorkbook){
        myWorkBook?.let{
            createFoodJson(it)
            createRecipeJson(it)
            createPlateJson(it)
            createPlanJson(it)
        }
    }

    private fun readExcelFile():XSSFWorkbook? {
        try {

            var myInput: InputStream;
            var assetManager = assets
            //  Don't forget to Change to your assets folder excel sheet
            Log.d("SHEET","Reading...........")
            myInput = assetManager.open("DietCalendar.xlsx");

            // Create a workbook using the File System
            var myWorkBook: XSSFWorkbook = XSSFWorkbook(myInput);
            return myWorkBook
            Log.d("SHEET","....Reading Complete....")

        } catch (e: Exception) {
            e.printStackTrace();
        }
        return null;
    }

    private fun createFoodJson(myWorkBook: XSSFWorkbook) {
        FOOD_MAX_ROW_INDEX = Integer.parseInt(tiv_tool_food?.editText?.text.toString()) - 1
        var array = fetchSheetJson(FOOD_SHEET_NAME, myWorkBook, FOOD_LAST_COLUMN_INDEX, FOOD_UNIT_ROW_INDEX, FOOD_START_ROW_INDEX, FOOD_MAX_ROW_INDEX)
        writeFile(FOOD_SHEET_NAME,array.toString(1))
    }

    private fun createRecipeJson(myWorkBook: XSSFWorkbook) {
        RECIPE_MAX_ROW_INDEX = Integer.parseInt(tiv_tool_recipe?.editText?.text.toString()) - 1
        var array = fetchSheetJson(RECIPE_SHEET_NAME,myWorkBook, RECIPE_LAST_COLUMN_INDEX,RECIPE_UNIT_ROW_INDEX,RECIPE_START_ROW_INDEX, RECIPE_MAX_ROW_INDEX)
        writeFile(RECIPE_SHEET_NAME,array.toString(1))
    }

    private fun createPlateJson(myWorkBook: XSSFWorkbook) {
        PLATE_MAX_ROW_INDEX = Integer.parseInt(tiv_tool_plate?.editText?.text.toString()) - 1
        var array = fetchSheetJson(PLATE_SHEET_NAME,myWorkBook, PLATE_LAST_COLUMN_INDEX,PLATE_UNIT_ROW_INDEX,PLATE_START_ROW_INDEX, PLATE_MAX_ROW_INDEX)
        writeFile(PLATE_SHEET_NAME,array.toString(1))
    }

    private fun createPlanJson(myWorkBook: XSSFWorkbook) {
        PLAN_MAX_ROW_INDEX = Integer.parseInt(tiv_tool_plan?.editText?.text.toString()) - 1
        var array = fetchSheetJson(PLAN_SHEET_NAME,myWorkBook, PLAN_LAST_COLUMN_INDEX,PLAN_UNIT_ROW_INDEX,PLAN_START_ROW_INDEX, PLAN_MAX_ROW_INDEX)
        writeFile(PLAN_SHEET_NAME,array.toString(1))
    }

    private fun fetchSheetJson(sheetName:String,myWorkBook:XSSFWorkbook,lastColumnIndex:Int,unitRowIndex:Int,startIndex:Int,endIndex:Int):JSONArray {
        Log.d("SHEET","Processing sheet :"+sheetName+" .........")
        // Get the first sheet from workbook
        var sheet: XSSFSheet = myWorkBook.getSheet(sheetName)

        var array = JSONArray()
        /** We now need something to iterate through the cells. **/

        for(index in startIndex..endIndex) {
            var rootHeader = Header("root", -1, 0, lastColumnIndex + 1)
            var sheetFormatJsonTree: TreeNode<Header> = TreeNode<Header>(rootHeader)

            addChildrenInTree(sheet, sheetFormatJsonTree)
            var obj = JSONObject()
            sheetFormatJsonTree.traverse(obj, sheet.getRow(index), sheet.getRow(unitRowIndex))
            var internalObj = obj.getJSONObject("root")
            array.put(internalObj)
        }

        return array;
    }

var mDir:String = Environment.DIRECTORY_DOCUMENTS;
var mPath :File = Environment.getExternalStoragePublicDirectory(mDir);

fun writeFile( filename:String,data:String) {
    var f:File =  File(mPath, filename + ".txt");
    f.getParentFile().mkdirs();
    try  {
        var bw :BufferedWriter= BufferedWriter( FileWriter(f, false))
        // Details omitted.
        bw.write(data)
        bw.close()
    } catch (e:Exception) {
        e.printStackTrace();
        return;
    }
    //Toast.makeText(this,"Wrote " + f.getAbsolutePath(),Toast.LENGTH_SHORT).show();
    scanFile(this,f,"Text")
}

    fun exportDBRecipeData(workbook: XSSFWorkbook,recipes:List<Recipe>){

        var recipeSheet = workbook.createSheet("recipe")

        var recipeRow = 0

        recipes?.forEach {
            var row = recipeSheet.createRow(recipeRow++)
            var cellData = getRecipeCellData(it)
            var recipeCell = 0
            cellData.forEach {
                var cell = row.createCell(recipeCell++)
                cell.setCellValue(it)
            }
        }


    }

    private fun getRecipeCellData(recipe: Recipe): ArrayList<String?> {
        var cellData = ArrayList<String?>()
        cellData.add(recipe.id)
        cellData.add(recipe.basicInfo?.name?.english)
        cellData.add(recipe.basicInfo?.name?.hindi)
        cellData.add(recipe.basicInfo?.desc)
        cellData.add(recipe.basicInfo?.image)
        cellData.add(recipe.basicInfo?.category)
        cellData.add(recipe.basicInfo?.subCategory)
        cellData.add(recipe.basicInfo?.type)
        cellData.add(""+recipe.basicInfo?.perServingCalories)
        cellData.add(""+recipe.standardServing?.qty)
        cellData.add(recipe.standardServing?.servingType)
        cellData.add(""+recipe.basicProperty?.availability)
        cellData.add("")
        cellData.add("")
        var gson = Gson()
        var prefMealJson = gson.toJson(recipe.basicProperty?.prefMeals)
        cellData.add(prefMealJson)

        cellData.add(gson.toJson(recipe.additionalInfo?.alerts))
        cellData.add(recipe.additionalInfo?.region)
        cellData.add(recipe.additionalInfo?.subRegion)
        cellData.add(gson.toJson(recipe.formula?.ingredients))
        cellData.add(gson.toJson(recipe.formula?.steps))
        cellData.add(recipe.adminInfo.createdOn)
        cellData.add(recipe.adminInfo.createdBy)
        cellData.add(recipe.adminInfo.featured?.toString())
        cellData.add(recipe.adminInfo.published?.toString())
        cellData.add(recipe.adminInfo.publishedOn)
        cellData.add(recipe.adminInfo.publishedBy)
        cellData.add(recipe.adminInfo.verified?.toString())
        cellData.add(recipe.adminInfo.verifiedOn)
        cellData.add(recipe.adminInfo.verifiedBy)
        cellData.add(recipe.adminInfo.lastModifiedOn)
        cellData.add(recipe.adminInfo.lastModifiedBy)

        return cellData
    }

    fun scanFile(ctxt: Context, f: File, mimeType: String) {
        MediaScannerConnection
                .scanFile(ctxt, arrayOf(f.absolutePath),
                        arrayOf(mimeType), null)
    }

    private fun addChildrenInTree(mySheet: XSSFSheet, parent: TreeNode<Header>) {
        //find cells from node.value.column  to node.value.column+range-1  at row node.value.row
        var row = parent.value.row+1
        var start = parent.value.column
        var end = start + parent.value.range-1//index

        if(parent.value.range <= 1){
            return
        }

        Log.d("TREE","finding children of "+parent.value.label+" From Column "+start+" to "+end+" At Row "+row)

        var itrCell = mySheet.getRow(row).cellIterator()
        //forward iterator till parent column index
       var currentIndex = 0
        while(itrCell.hasNext() && currentIndex < start){
            currentIndex = itrCell.next().columnIndex
        }
        Log.i("TREE","Fast Forwarded till:"+currentIndex)
        var isFirstCell = true

        do{
            var thisCell:Cell? = null
            if(isFirstCell){
                isFirstCell=false
                thisCell = mySheet.getRow(row).getCell(currentIndex)
            } else {
                thisCell = itrCell.next()
            }

            var label = getLabel(thisCell as XSSFCell)
            Log.d("TREE","For "+parent.value.label+", Found:"+label+" at column:"+thisCell.columnIndex)
            //find range of children of this cell to add
            var it = mySheet.getRow(row).cellIterator()
            var nextCell:XSSFCell? = null;

            while(it.hasNext()) {
                var c = it.next()
                if (c.columnIndex == thisCell.columnIndex) {
                    if(it.hasNext()) {
                        nextCell = it.next() as XSSFCell
                    } else {
                        nextCell = null
                    }
                    break;
                }
            }

            var range = 0
            if(nextCell == null){
                range = end- thisCell!!.columnIndex
            } else {
                range = nextCell!!.columnIndex - thisCell!!.columnIndex
            }

            //found new header, add to its parent

            var header = Header(toCamelCase(label),thisCell.rowIndex,thisCell.columnIndex,range)
            var child = TreeNode(header)
            parent.addChild(child)
            //add newly added cell's children
            addChildrenInTree(mySheet,child)
            if(thisCell.columnIndex + range-1 == end){
                break;
            }

        }while(itrCell.hasNext() && thisCell!!.columnIndex < end)

    }

    private fun getLabel(thisCell: XSSFCell): String {
        var value:String
        if (!TextUtils.isEmpty(thisCell.toString()) && (thisCell.toString().startsWith("LEFT")
                || thisCell.toString().startsWith("RIGHT")
                || thisCell.toString().startsWith("IFERROR")
                || thisCell.toString().startsWith("Sheet")
                || thisCell.toString().startsWith("f!"))) {
            value = thisCell.rawValue
        } else {
            value = thisCell.toString()
        }
        return value
    }


    fun toCamelCase(init: String): String {
        if (TextUtils.isEmpty(init)) return "";
        val ret = StringBuilder(init.length)

        var i = 0
        for (word in init.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()) {
            if (!word.isEmpty()) {
                if (i == 0) {
                    ret.append(word.substring(0, 1).toLowerCase())
                } else {
                    ret.append(word.substring(0, 1).toUpperCase())
                }
                ret.append(word.substring(1).toLowerCase())
                i++
            }
//            if (ret.length != init.length)
//                ret.append(" ")
        }

        return ret.toString()
    }

    private fun createFileFromInputStream(inputStream: InputStream): File? {

        try {
            var f: File = File("" + externalCacheDir + "/Documents/diet_calendar_1.xlxs");
            Log.d("EXCEL", "Creating file at :" + f.absolutePath)
            var outputStream: OutputStream = FileOutputStream(f);
            var buffer: ByteArray = ByteArray(1024 * 3)
            var length = 0;
            length = inputStream.read(buffer)
            while (length > 0) {
                outputStream.write(buffer, 0, length);
                length = inputStream.read(buffer)
            }

            outputStream.close();
            inputStream.close();

            return f;
        } catch (e: IOException) {
            //Logging exception
        }

        return null;
    }

    /**
     * Add random stuff to a spreadsheet to make sure everything works
     */
    private fun Sheet.populate() {
        createFreezePane(1, 1)
        createRow(0).apply {
            createCell(0).apply {
                setCellValue(0.0)
                cellStyle = workbook.createCellStyle().apply {
                    dataFormat = workbook.createDataFormat().getFormat("0.00")
                    setFont(workbook.createFont().apply { bold = true })
                    setAlignment(HorizontalAlignment.CENTER)
                    setVerticalAlignment(VerticalAlignment.CENTER)
                }
            }
            createCell(1).apply {
                setCellValue("Wassup?")
            }
            createCell(2).apply {
                createRow(1).createCell(0).setCellValue(1.0)
                cellFormula = "SUM(${CellRangeAddress(0, 1, 0, 0).formatAsString()})"
            }
        }

        createDrawingPatriarch().run {
            createChart(createAnchor(0, 0, 0, 0, 4, 2, 10, 6))
        }.apply {
            orCreateLegend.position = LegendPosition.RIGHT

            chartDataFactory.createLineChartData().apply {
                addSeries(
                        DataSources.fromStringCellRange(
                                this@populate,
                                CellRangeAddress(0, 0, 1, 1)
                        ),
                        DataSources.fromNumericCellRange(
                                this@populate,
                                CellRangeAddress(0, 1, 0, 0)
                        )
                ).setTitle("Foobar")
                plot(
                        this,
                        chartAxisFactory.createCategoryAxis(AxisPosition.BOTTOM),
                        chartAxisFactory.createValueAxis(AxisPosition.LEFT).apply {
                            crosses = AxisCrosses.AUTO_ZERO
                        }
                )
            }

            if (this is XSSFChart) {
                val plotArea = ctChart.plotArea
                plotArea.getValAxArray(0).addNewTitle().setValue("Values")
                plotArea.getCatAxArray(0).addNewTitle().setValue("Title")
            }
        }
    }

    private fun CTTitle.setValue(text: String) {
        addNewLayout()
        addNewOverlay().`val` = false

        val textBody = addNewTx().addNewRich()
        textBody.addNewBodyPr()
        textBody.addNewLstStyle()

        val paragraph = textBody.addNewP()
        paragraph.addNewPPr().addNewDefRPr()
        paragraph.addNewR().t = text
        paragraph.addNewEndParaRPr()
    }

    companion object {
        init {
            System.setProperty(
                    "org.apache.poi.javax.xml.stream.XMLInputFactory",
                    "com.fasterxml.aalto.stax.InputFactoryImpl"
            )
            System.setProperty(
                    "org.apache.poi.javax.xml.stream.XMLOutputFactory",
                    "com.fasterxml.aalto.stax.OutputFactoryImpl"
            )
            System.setProperty(
                    "org.apache.poi.javax.xml.stream.XMLEventFactory",
                    "com.fasterxml.aalto.stax.EventFactoryImpl"
            )
        }
    }
}
