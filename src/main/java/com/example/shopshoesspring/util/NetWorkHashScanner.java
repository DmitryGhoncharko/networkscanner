package com.example.shopshoesspring.util;

import com.example.shopshoesspring.entity.Hash;
import com.example.shopshoesspring.entity.Log;
import com.example.shopshoesspring.repository.HashRepository;
import com.example.shopshoesspring.repository.LogRepository;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class NetWorkHashScanner {
    private final HashRepository hashRepository;
    private final LogRepository logRepository;
    public void deleteByPath(String path) {
        String smbPath = path;

        try {
            SmbFile smbFile = new SmbFile(smbPath);

            if (smbFile.exists()) {
                smbFile.delete();
            } else {
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public List<Log> scanHash(String ip, String login, String password, String path){
        List<Log> logs = new ArrayList<>();
        String localPath = "smb://"+ login + ":" + password+"@" + ip+ path;
        System.out.println("############################################" + localPath);
        try {
            SmbFile smbFile = new SmbFile(localPath);

            if (smbFile.exists()) {
              logs =  scanFiles(smbFile,ip,localPath);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return logs;
    }
    private List<Log> scanFiles(SmbFile smbFile, String ip, String path) throws IOException, NoSuchAlgorithmException {
        List<Log> logs = new ArrayList<>();
        List<Hash> hashList = hashRepository.findAll();
        if (smbFile == null) {
            return logs;
        }
        if (smbFile.isDirectory()) {
            SmbFile[] files = smbFile.listFiles();
            for (SmbFile file : files) {
                if (file.isDirectory()) {
                    scanFiles(file,ip,path);
                } else {
                    String hash = calculateFileHash(file);
                    System.out.println(ip + " " + hash);
                    if(hash != null ){
                       for(Hash hashObj : hashList){
                           if(hashObj.getHashValue().equals(hash)){
                               Log log = new Log();
                               log.setDate(Instant.now());
                               log.setIp(ip);
                               log.setHash(hashObj);
                               log.setPath(path + file.getName());
                               System.out.println(path + file.getName());
                               log.setMask("#####");
                               logs.add(log);
                               logRepository.save(log);
                           }
                       }
                    }
                }
            }
        }
        return logs;
    }

    private String calculateFileHash(SmbFile file) throws IOException, NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("MD5");
        try (SmbFileInputStream inputStream = new SmbFileInputStream(file)) {
            byte[] buffer = new byte[999999999];
            int read;
            while ((read = inputStream.read(buffer)) != -1) {
                digest.update(buffer, 0, read);
            }
        }
        byte[] hashBytes = digest.digest();
        StringBuilder hexString = new StringBuilder();
        for (byte hashByte : hashBytes) {
            String hex = Integer.toHexString(0xff & hashByte);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
