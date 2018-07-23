package com.jtv.parse;

import com.jtv.config.ConfigProperties;
import com.jtv.publish.PublishService;
import com.supermap.data.Dataset;
import com.supermap.data.Datasource;
import com.supermap.data.Maps;
import com.supermap.data.Workspace;
import com.supermap.data.conversion.DataImport;
import com.supermap.data.conversion.ImportMode;
import com.supermap.data.conversion.ImportResult;
import com.supermap.data.conversion.ImportSettingDWG;
import com.supermap.mapping.Layers;
import com.supermap.mapping.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FilenameFilter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author lianrongfa
 * @date 2018/7/17
 */
public class ImportDwg extends Source{

    private final static Logger logger= LoggerFactory.getLogger(ImportDwg.class);

    //private ExecutorService executorService=Executors.newFixedThreadPool(4);

    public static void main(String[] args) {

        new ImportDwg().parse();
    }


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

                /*Runnable task = new Runnable(){
                    public void run(){*/
               importDWG(getDs(),absolutePath);
                        //countDownLatch.countDown();
               file.delete();
                   /* }
                };
                executorService.execute(task);*/

            }
            /*try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/
            logger.info("解析"+list.length+"个文件用时："+(System.currentTimeMillis()-time1));
        }
    }

    /**
     * @param dataset 数据集
     */
    public void buildMap(Dataset dataset){

        Maps maps = getWorkspace().getMaps();
        String datasetName = dataset.getName();

        try{
            maps.remove(datasetName);
        }catch (Exception e){
        }

        Map map = new Map(getWorkspace());
        //map.setScale(0.002);
        Layers layers = map.getLayers();
        layers.add(dataset, true);

        maps.add(dataset.getName(),map.toXML());

    }


    public void importDWG(Datasource ds, String file){

        ImportSettingDWG importSettingDWG = new ImportSettingDWG();

        importSettingDWG.setImportMode(ImportMode.OVERWRITE);
        importSettingDWG.setTargetDatasource(ds);
        importSettingDWG.setSourceFilePath(file);

        DataImport dataImport = new DataImport();
        dataImport.getImportSettings().add(importSettingDWG);

        ImportResult result = dataImport.run();
        String[] names = result.getSucceedDatasetNames(importSettingDWG);

        if(names!=null&&names.length>0){
            Dataset dataset = getDs().getDatasets().get(names[0]);
            buildMap(dataset);
        }
    }
}
