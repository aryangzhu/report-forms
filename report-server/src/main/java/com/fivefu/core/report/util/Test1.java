package com.fivefu.core.report.util;


import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * 从https://gitcode.net/tasking/Encrypt-decrypt-files/-/raw/master/AES.zip 网址下载AES.zip，
 * 编写程序解压AES.zip，
 * 将得到若干经过先经过AES算法加密，
 * 然后经过Base64编码后的文件，
 * 秘钥为1111222233334444，加密模式为CBC，偏移量为5555666677778888，
 * 请解密文件中的内容，并且重新打包成一个压缩包。
 */
public class Test1 {
    public static void main(String[] args) throws MalformedURLException {
//        String url = "http://192.168.0.21:9000/illegalevent/zip/2023-03-21/caseAudit.jrxml-20230321060135.zip";
//        DownAndReadFile(url);
        URL url=new File("/Users/liulei/data/template/1679469318019.html").toURI().toURL();
        System.out.println(url);
    }

    /**
     * 远程文件下载地址
     *
     * @param filePath 网络文件请求地址
     */
    public static void DownAndReadFile(String filePath) {
        long startTime = System.currentTimeMillis();
        // 获取的年月日对象信息
        String data = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        //创建一个下载文件的文件路径
        String dir = "/Users/liulei/data/template" + data;
        // 声明文件对象
        File saverPath = new File(dir);
        // 判断文件是否存在
        if (!saverPath.exists()) {
            // 文件不存在就创建一个一级目录【远程请求下载】
            saverPath.mkdir();
        }
        // 根据/切割接受到的请求网络URL
        String[] urlName = filePath.split("/");
        // 获取到切割的字符串数组长度-1
        int len = urlName.length - 1;
        // 获取到请求下载文件的名称
        String uname = urlName[len];
        // System.out.println(uname); // AES.zip

        // 跳过try捕获错误
        try {
            // 创建保存文件对象
            File file = new File(saverPath + "/" + uname);//创建新文件
            if (file != null && !file.exists()) {
                file.createNewFile();
            }
            // 通过高效字节输出流输出创建的文件对象
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(file));
            // 创建URL对象[请求路径]
            URL url = new URL(filePath);
            // 返回一个URLConnection实例，表示与URL引用的远程对象的URL
            HttpURLConnection uc = (HttpURLConnection) url.openConnection();
            uc.setDoInput(true); // 设置的值 doInput领域本 URLConnection指定值。
            uc.connect(); // 打开与此URL引用的资源的通信链接，如果此类连接尚未建立。
            // 获取服务端的字节输入流
            InputStream inputStream = uc.getInputStream();
            System.out.println("file size is：" + uc.getContentLength()); // 打印文件的长度
            // 声明字节数组存放读取的文件
            byte[] b = new byte[1024 * 4];
            int byteRead = -1; // 定义读取次数
            // 循环读取
            while ((byteRead = inputStream.read(b)) != -1) {
                bufferedOutputStream.write(b, 0, byteRead); // 将读取的文件跳过高效的字节流输出
            }
            // 关闭流和刷新流
            inputStream.close();
            bufferedOutputStream.close();
            long endTime = System.currentTimeMillis();
            System.out.println("下载耗时：" + (endTime - startTime) / 1000 * 1.0 + "s");
            System.out.println("文件下载成功！");

            // ---------- 解压文件 ----------
            StringBuffer strb = new StringBuffer();
            // 创建高效的字节输入管道
            BufferedInputStream fs = new BufferedInputStream(new FileInputStream(saverPath + "/" + uname));
            BufferedReader br = new BufferedReader(new InputStreamReader(fs, "UTF-8")); // 指定读取的编码格式); // 高效缓存字节读取
            String date = ""; // 记录读取一行的数据
            // 循环读取
            while ((date = br.readLine()) != null) {
                strb.append(data + " "); // 将读取的数据赋值给可变的字符串
            }
            // 关闭相关的流
            br.close();
            fs.close();
            System.out.println("解压文件中...");
            //解压
            unZipFiles(dir + "/AES.zip", dir + "/");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 解压的文件
     *
     * @param zipPath 压缩文件
     * @param descDir 解压存放的位置
     * @throws Exception
     */
    public static void unZipFiles(String zipPath, String descDir) throws Exception {
        System.out.println("解压文件的名称：" + zipPath + " 解压的文件存放路径：" + descDir);
                unZipFiles(new File(zipPath), descDir); // 调用方法
    }

    /**
     * 解压文件到指定的位置
     *
     * @param zipFile 解压文件
     * @param descDir 存放目录
     */
    @SuppressWarnings("rawtypes") // 抑制警告【原始类型】
    public static void unZipFiles(File zipFile, String descDir) throws Exception {
        // 创存放文件的对象
        File pathFile = new File(descDir);
        // 判断文件是否存在
        if (!pathFile.exists()) {
            // 创建目录[不找到压缩文件里的内容，所以需要创建多级目录]
            pathFile.mkdirs();
        }
        // 创建压缩包条目
        ZipFile zip = new ZipFile(zipFile); // 此类用于从zip文件读取条目。
        // entries() 打开一个ZIP文件，读取指定的File对象。
        for (Enumeration entries = zip.entries(); entries.hasMoreElements(); ) {
            ZipEntry entry = (ZipEntry) entries.nextElement(); // 获取条目
            String zipEntryMame = entry.getName(); // 获取条目名
            InputStream in = zip.getInputStream(entry); // 获取文件的输入流
            String outPath = (descDir + zipEntryMame).replaceAll("\\*", "//"); // 替换全部
            // 判断路径是否存在
            File file = new File(outPath.substring(0, outPath.lastIndexOf("/")));
            // 判断文件，不存在就创建
            if (!file.exists()) {
                file.mkdirs(); // 多级目录
            }
            // 判断文件路径是否为文件
            if (new File(outPath).isDirectory()) {
                continue;
            }
            // 输出文件的路径
            System.out.println(outPath);
            // 创建字节输出流
            FileOutputStream out = new FileOutputStream(outPath);
            // 创建字节数组
            byte[] byf1 = new byte[1024];
            int len;
            while ((len = in.read(byf1)) != -1) {
                out.write(byf1);
            }
            // 关闭流
            in.close();
            out.close();
        }
        System.out.println("文件解压成功");
    }

}

