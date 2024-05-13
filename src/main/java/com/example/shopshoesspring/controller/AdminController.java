package com.example.shopshoesspring.controller;


import com.example.shopshoesspring.entity.Delayed;
import com.example.shopshoesspring.entity.Hash;
import com.example.shopshoesspring.entity.Log;
import com.example.shopshoesspring.repository.DelayedRepository;
import com.example.shopshoesspring.repository.HashRepository;
import com.example.shopshoesspring.repository.LogRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.example.shopshoesspring.util.NetWorkHashScanner;
import com.example.shopshoesspring.util.SMBScanner;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {
    private final HashRepository hashRepository;
    private final LogRepository logRepository;
    private final NetWorkHashScanner netWorkHashScanner;
    private final SMBScanner smbScanner;
    private final DelayedRepository delayedRepository;
    @GetMapping("/home")
    public String homePage() {
        return "ahome";
    }

    @GetMapping("/addHash")
    public String addHashPage() {
        return "addhash";
    }
    @PostMapping("/addHash")
    public String addHash(@RequestParam("hashValue") String hashValue) {
        Hash hash = new Hash();
        hash.setHashValue(hashValue);
        hashRepository.save(hash);
        return "redirect:/admin/addHash";
    }
    @GetMapping("/hashList")
    public String hashListPage(Model model) {
        model.addAttribute("hashList", hashRepository.findAll());
        return "hashlist";
    }
    @PostMapping("/deleteHash")
    public String deleteHash(@RequestParam("hashId") Long hashId) {
      try{
          hashRepository.deleteById(hashId);
      }catch (Throwable e){
          return "redirect:/admin/cannotDel";
      }
        return "redirect:/admin/hashList";
    }
    @GetMapping("/cannotDel")
    public String cannotDelHash() {
        return "cannotdelhash";
    }
    @GetMapping("/updateHash/{hashId}")
    public String updateHashPage(@PathVariable("hashId") Long hashId, Model model) {
        Hash hash = hashRepository.findById(hashId).orElse(null);
        if (hash == null) {
            return "redirect:/admin/hashList";
        }
        model.addAttribute("hash", hash);
        return "updatehash";
    }

    @PostMapping("/updateHash")
    public String updateHash(@RequestParam("hashId") Long hashId,
                             @RequestParam("newHashValue") String newHashValue) {
        Hash hash = hashRepository.findById(hashId).orElse(null);
        if(hash != null) {
            hash.setHashValue(newHashValue);
            hashRepository.save(hash);
        }
        return "redirect:/admin/hashList";
    }
    @GetMapping("/scan")
    public String scanPage() {
        return "scan";
    }
    @GetMapping("/discoverSMBNetworks")
    public String discoverSMBNetworks(@RequestParam("subnet") String subnet, Model model) {
        List<String> smbIps = smbScanner.scanNetwork(subnet);
        model.addAttribute("smbIps", smbIps);
        model.addAttribute("ipSMB",subnet);
        return "discoverSMBNetworksPage";
    }
    @GetMapping("/startScan")
    public String startScan(@RequestParam("subnet") String subnet,
                                  @RequestParam("path") String path,
                                  @RequestParam("login") String login,
                                  @RequestParam("password") String password,
                            Model model) {
        List<Log> logs = netWorkHashScanner.scanHash(subnet,login,password,path);
        model.addAttribute("logs", logs);
        return "scanResult";
    }
    @GetMapping("/startDelayedScan")
    public String startDelayedScan() {
        return "startDelayedScan";
    }
    @GetMapping("/startScanWithPath")
    public String startScanWithPath(@RequestParam("ipSMB") String ipSMB,
                                    @RequestParam("path") String path,
                                    @RequestParam("login") String login,
                                    @RequestParam("password") String password,
                                    Model model) {

        List<String> smbIps = smbScanner.scanNetwork(ipSMB);
        List<Log> logs = new ArrayList<>();
        for(String smbIp : smbIps) {
            logs.addAll(netWorkHashScanner.scanHash(smbIp,login,password,path));
        }
        model.addAttribute("logs", logs);
        return "scanResult";
    }
    @GetMapping("/delete")
    @ResponseBody
    @Transactional
    public String deleteFile(@RequestParam("path") String path) {
        netWorkHashScanner.deleteByPath(path);
        logRepository.deleteByPath(path);
        return "success";
    }
    @GetMapping("/discoverSMBNetworksDelayed")
    public String discoverSMBNetworksDelayed(@RequestParam("subnet") String subnet, Model model,  @RequestParam("scanDateTime") String scanDateTime) {
        List<String> smbIps = smbScanner.scanNetwork(subnet);
        model.addAttribute("smbIps", smbIps);
        model.addAttribute("ipSMB",subnet);
        model.addAttribute("scanDateTime", scanDateTime);
        return "discoverSMBNetworksPageDelayed";
    }
    @GetMapping("/startScanDelayed")
    public String startScanDelayed(
            @RequestParam("subnet") String subnet,
            @RequestParam("path") String path,
            @RequestParam("login") String login,
            @RequestParam("password") String password,
            @RequestParam(value = "scanDateTime", required = true) String scanDateTime,
            Model model
    ) {
        Delayed delayed = new Delayed();
        delayed.setSubnet(subnet);
        delayed.setPath(path);
        delayed.setLogin(login);
        delayed.setPassword(password);
        LocalDateTime localDateTime = LocalDateTime.parse(scanDateTime, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));
        Instant instant = localDateTime.toInstant(ZoneOffset.UTC);
        delayed.setScanDate(instant);
        delayed.setIpSmb("EMPTY");
        delayedRepository.save(delayed);
        return "redirect:/admin/dl";
    }
    @GetMapping("/startScanWithPathDelayed")
    public String startScanWithPathDelayed(
            @RequestParam("ipSMB") String ipSMB,
            @RequestParam("path") String path,
            @RequestParam("login") String login,
            @RequestParam("password") String password,
            @RequestParam(value = "scanDateTime", required = false) String scanDateTime,
            Model model
    ) {
        Delayed delayed = new Delayed();
        delayed.setSubnet("EMPTY");
        delayed.setPath(path);
        delayed.setLogin(login);
        delayed.setPassword(password);
        LocalDateTime localDateTime = LocalDateTime.parse(scanDateTime, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));
        Instant instant = localDateTime.toInstant(ZoneOffset.UTC);
        delayed.setScanDate(instant);
        delayed.setIpSmb(ipSMB);
        delayedRepository.save(delayed);
        return "redirect:/admin/dl";
    }
    @GetMapping("/logs")
    public String logsPage(Model model) {
        model.addAttribute("logs", logRepository.findAll());
        return "scanResult";
    }
    @GetMapping("/dl")
    public String dlPage(Model model) {
        model.addAttribute("dl", delayedRepository.findAll());
        return "dl";
    }
    @GetMapping("/deleteDelayed")
    public String deleteDelayed(@RequestParam("id") Long id) {
        delayedRepository.deleteById(id);
        return "redirect:/admin/dl";
    }
}
