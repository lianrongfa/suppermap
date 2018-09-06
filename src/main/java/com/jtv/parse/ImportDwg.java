package com.jtv.parse;

import com.jtv.config.ConfigProperties;
import com.jtv.publish.PublishService;
import com.supermap.data.*;
import com.supermap.data.conversion.DataImport;
import com.supermap.data.conversion.ImportMode;
import com.supermap.data.conversion.ImportResult;
import com.supermap.data.conversion.ImportSettingDWG;
import com.supermap.mapping.Layer;
import com.supermap.mapping.Layers;
import com.supermap.mapping.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.File;
import java.io.FilenameFilter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 用于导入cad文件数据
 * @author lianrongfa
 * @date 2018/7/17
 */
public class ImportDwg extends Source{

    private final static Logger logger= LoggerFactory.getLogger(ImportDwg.class);

    public static void main(String[] args) {

        new ImportDwg().parse();
    }


    /**
     * 读取文件解析
     */
    public void parse(){
        ConfigProperties configProperties = getConfigProperties();

        File directory = new File(configProperties.getFile());

        boolean b = directory.isDirectory();

        if(!b){
           logger.error("不是一个目录："+configProperties.getFile());
           return ;
        }
        doParse(directory);

        Workspace workspace = getWorkspace();
        workspace.save();
        workspace.close();
        workspace.dispose();

        new PublishService().publish();
    }

    private void doParse(File directory) {
        File[] list = directory.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                Pattern pattern = Pattern.compile(".*(.dwg)$");

                Matcher matcher = pattern.matcher(name);

                return matcher.matches();
            }
        });
        if(list!=null){
            //final CountDownLatch countDownLatch = new CountDownLatch(list.length);
            long time1 = System.currentTimeMillis();
            for (final File file : list) {
                final String absolutePath = file.getAbsolutePath();
                importDWG(getDs(),absolutePath);
                file.delete();
            }

            logger.info("解析"+list.length+"个文件用时："+(System.currentTimeMillis()-time1));
        }else{
            logger.info("暂时没有文件...");
        }
    }


    /**
     * 将cad文件数据转为suppermap支持的数据
     * @param ds 导入的数据源
     * @param file  cad文件路径
     */
    private void importDWG(Datasource ds, String file){

        ImportSettingDWG importSettingDWG = new ImportSettingDWG();

        importSettingDWG.setImportMode(ImportMode.OVERWRITE);
        importSettingDWG.setTargetDatasource(ds);
        importSettingDWG.setSourceFilePath(file);

        DataImport dataImport = new DataImport();
        dataImport.getImportSettings().add(importSettingDWG);

        ImportResult result = dataImport.run();
        String[] names = result.getSucceedDatasetNames(importSettingDWG);

        if(names!=null&&names.length>0){
            Datasets datasets = ds.getDatasets();

            Dataset dataset = datasets.get(names[0]);

            //新增一个空白面数据集
            String addRegion=names[0]+"_region";
            Dataset vector = datasets.get(addRegion);
            if(vector==null){

                DatasetVectorInfo datasetVectorInfo = new DatasetVectorInfo();
                datasetVectorInfo.setType(DatasetType.REGION);
                datasetVectorInfo.setName(addRegion);
                vector = datasets.create(datasetVectorInfo);
                // 释放资源
                datasetVectorInfo.dispose();
            }

            buildMap(dataset,vector,true);

            vector.close();
            dataset.close();
        }
    }

    /**
     *
     * @param dataset cad数据集
     * @param region 面数据集
     * @param ensureVisible cad是否全幅显示
     */
    private void buildMap(Dataset dataset,Dataset region,boolean ensureVisible){

        Maps maps = getWorkspace().getMaps();
        String datasetName = dataset.getName();

        try{
            maps.remove(datasetName);
        }catch (Exception e){
        }

        Map map = new Map(getWorkspace());
        Layers layers = map.getLayers();
        Layer layer = layers.add(dataset, true);
        layers.add(region, true);
        if(ensureVisible){
            map.ensureVisible(layer);
            map.getBackgroundStyle().setFillForeColor(Color.BLACK);

        }
        maps.add(dataset.getName(),map.toXML());

    }
}
