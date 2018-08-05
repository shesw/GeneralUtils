package sinaUtils;
import java.io.*;
import java.net.URL;
import java.util.*;

import com.sina.cloudstorage.auth.AWSCredentials;
import com.sina.cloudstorage.auth.BasicAWSCredentials;
import com.sina.cloudstorage.event.ProgressEvent;
import com.sina.cloudstorage.event.ProgressListener;
import com.sina.cloudstorage.services.scs.SCS;
import com.sina.cloudstorage.services.scs.SCSClient;
import com.sina.cloudstorage.services.scs.model.AccessControlList;
import com.sina.cloudstorage.services.scs.model.Bucket;
import com.sina.cloudstorage.services.scs.model.ObjectListing;
import com.sina.cloudstorage.services.scs.model.ObjectMetadata;
import com.sina.cloudstorage.services.scs.model.Permission;
import com.sina.cloudstorage.services.scs.model.PutObjectRequest;
import com.sina.cloudstorage.services.scs.model.PutObjectResult;
import com.sina.cloudstorage.services.scs.model.S3Object;
import com.sina.cloudstorage.services.scs.model.UserIdGrantee;
import com.sina.cloudstorage.services.scs.transfer.ObjectMetadataProvider;


public class SinaStoreSDK {
	
	private String accessKey = "2o3w9tlWumQRMwg2TQqi";
	private String secretKey = "01a03965e29bed4a51f51f57d10f4c60ba68a050";
	private AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
	private SCS conn = new SCSClient(credentials);
	
	/* ����url*/
	public String generateUrl(String bucketName, String path, int minutes){
	    Date expiration = new Date();       //����ʱ��
	    long epochMillis = expiration.getTime();
	    epochMillis += 1000*60*minutes;
	    expiration = new Date(epochMillis);   
	    URL presignedUrl = conn.generatePresignedUrl(bucketName, path, expiration, false);
	    return presignedUrl.toString();
	}

	/**
	 * 获取所有bucket
	 */
	public List<Bucket> getAllBuckets(){
	    List<Bucket> list = conn.listBuckets();
	    return list;
	}
	
	
	/**
	 * 创建bucket
	 */
	public void createBucket(String bucketName){
	    Bucket bucket = conn.createBucket(bucketName);
	    //System.out.println(bucket);
	}
	
	/**
	 * 删除bucket
	 */
	public void deleteBucket(){
	    conn.deleteBucket("create-a-bucket");
	}
	
	/**
	 * 获取bucket ACL
	 */
	public void getBucketAcl(){
	    AccessControlList acl = conn.getBucketAcl("create-a-bucket");
	    //System.out.println(acl);
	}
	
	/**
	 * 设置bucket acl
	 */
	public void putBucketAcl(){
	    AccessControlList acl = new AccessControlList();
	    acl.grantPermissions(UserIdGrantee.CANONICAL, Permission.Read, Permission.ReadAcp);
	    acl.grantPermissions(UserIdGrantee.ANONYMOUSE, 
	                        Permission.ReadAcp,
	                        Permission.Write,
	                        Permission.WriteAcp);
	    acl.grantPermissions(new UserIdGrantee("UserId"), 
	                        Permission.Read,
	                        Permission.ReadAcp,
	                        Permission.Write,
	                        Permission.WriteAcp);

	    conn.setBucketAcl("create-a-bucket", acl);
	}
	
	
	/**
	 * 列bucket中所有文件
	 */
	public ObjectListing listObjects(String bucketName){
	    ObjectListing objectListing = conn.listObjects(bucketName);
	    //System.out.println(objectListing);
	    return objectListing;
	}
	
	
	/**
	 * 获取object metadata
	 */
	public ObjectMetadata getObjectMeta(String bucketName, String path){
	    ObjectMetadata objectMetadata = conn.getObjectMetadata(bucketName, path);
//	    System.out.println(objectMetadata.getUserMetadata());
//	    System.out.println(objectMetadata.getContentLength());
//	    System.out.println(objectMetadata.getRawMetadata());
//	    System.out.println(objectMetadata.getETag());
	    return objectMetadata;
	}
	
	
	/**
	 * 下载object 
	 *  //断点续传
	 *  GetObjectRequest rangeObjectRequest = new GetObjectRequest("test11", "/test/file.txt");
	 *  rangeObjectRequest.setRange(0, 10); // retrieve 1st 10 bytes.
	 *  S3Object objectPortion = conn.getObject(rangeObjectRequest);
	 *          
	 *  InputStream objectData = objectPortion.getObjectContent();
	 *  // "Process the objectData stream.
	 *  objectData.close();
	 */
	public void getObject(String bucketName, String path, String savePath) throws FileNotFoundException{
	    //SDKGlobalConfiguration.setGlobalTimeOffset(-60*5);//自定义全局超时时间5分钟以后(可选项)
	    S3Object s3Obj = conn.getObject(bucketName, path);
	    InputStream in = s3Obj.getObjectContent();
	    byte[] buf = new byte[1024];
	    OutputStream out = null;
	    try {
	        out = new FileOutputStream(new File(savePath));
	        int count;
	        while( (count = in.read(buf)) != -1)
	        {
	           if( Thread.interrupted() )
	           {
	               throw new InterruptedException();
	           }
	           out.write(buf, 0, count);
	        }
	        System.out.println("下载成功");
	    } catch (IOException e) {
	        e.printStackTrace();
	    } catch (InterruptedException e) {
	        e.printStackTrace();
	    }finally{
	        //SDKGlobalConfiguration.setGlobalTimeOffset(0);//还原超时时间
	        try {
	            out.close();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	        try {
	            in.close();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	}
	
	/**
	 * 上传文件
	 */
	public void putObject(String bucketName, String path, String fileName){
	    PutObjectResult putObjectResult = conn.putObject(bucketName,
	                                        path, new File(fileName));
	    //System.out.println(putObjectResult);
	}
	
	public void putObject(String bucketName, String path, File file){
	    PutObjectResult putObjectResult = conn.putObject(bucketName,
	                                        path, file);
	    //System.out.println(putObjectResult);
	}
	

//	/**
//	 * 上传文件--进度回调方法
//	 */
//	public void putObject(String bucketName, String path, String fileName){    
//	    PutObjectRequest por = new PutObjectRequest(bucketName, path, 
//	            new File(fileName)).withMetadata(new ObjectMetadata());
//	    por.setGeneralProgressListener(new ProgressListener() {
//	        @Override
//	        public void progressChanged(ProgressEvent progressEvent) {
//	            // TODO Auto-generated method stub
//	            System.out.println(progressEvent);
//	        }
//	    });
//
//	    PutObjectResult putObjectResult = conn.putObject(por);
//	    System.out.println(putObjectResult);
//
//	}
	
	/**
	 * 上传文件 自定义请求头
	 */
	public void putObjectWithCustomRequestHeader(String bucketName, String path, String fileName){
	    //自定义请求头k-v
	    Map<String, String> requestHeader = new HashMap<String, String>();
	    requestHeader.put("Content-type", "text/html;charset=utf-8");
	    PutObjectResult putObjectResult = conn.putObject(bucketName, path, 
	                                                      new File(fileName), requestHeader);
	    System.out.println(putObjectResult);//服务器响应结果
	}
	
	/**
	 * 删除Object
	 */
	public void deleteObject(String bucketName, String path){
	    conn.deleteObject(bucketName, path);
	}    
	
}
