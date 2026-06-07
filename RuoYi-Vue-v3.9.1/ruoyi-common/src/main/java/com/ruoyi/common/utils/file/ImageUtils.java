package com.ruoyi.common.utils.file;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import org.apache.poi.util.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.utils.StringUtils;

/**
 * 图片处理工具类
 *
 * @author ruoyi
 */
public class ImageUtils
{
    private static final Logger log = LoggerFactory.getLogger(ImageUtils.class);

    public static byte[] getImage(String imagePath)
    {
        InputStream is = getFile(imagePath);
        try
        {
            return IOUtils.toByteArray(is);
        }
        catch (Exception e)
        {
            log.error("图片加载异常 {}", e);
            return null;
        }
        finally
        {
            IOUtils.closeQuietly(is);
        }
    }

    public static InputStream getFile(String imagePath)
    {
        try
        {
            byte[] result = readFile(imagePath);
            result = Arrays.copyOf(result, result.length);
            return new ByteArrayInputStream(result);
        }
        catch (Exception e)
        {
            log.error("获取图片异常 {}", e);
        }
        return null;
    }

    /**
     * 读取文件为字节数据
     * 
     * @param url 地址
     * @return 字节数据
     */
    public static byte[] readFile(String url)
    {
        InputStream in = null;
        try
        {
            if (url.startsWith("http"))
            {
                // SSRF 验证
                if (!isSafeUrl(url))
                {
                    log.warn("URL has SSRF risk, block it: {}", url);
                    return null;
                }
                // 网络地址
                URL urlObj = new URL(url);
                URLConnection urlConnection = urlObj.openConnection();
                urlConnection.setConnectTimeout(30 * 1000);
                urlConnection.setReadTimeout(60 * 1000);
                urlConnection.setDoInput(true);
                in = urlConnection.getInputStream();
            }
            else
            {
                // 本机地址
                String localPath = RuoYiConfig.getProfile();
                String downloadPath = localPath + StringUtils.substringAfter(url, Constants.RESOURCE_PREFIX);
                in = new FileInputStream(downloadPath);
            }
            return IOUtils.toByteArray(in);
        }
        catch (Exception e)
        {
            log.error("获取文件路径异常 {}", e);
            return null;
        }
        finally
        {
            IOUtils.closeQuietly(in);
        }
    }

    /**
     * 验证URL是否安全以防范SSRF攻击
     *
     * @param url 待验证的URL
     * @return 如果安全返回true，否则返回false
     */
    private static boolean isSafeUrl(String url)
    {
        try
        {
            if (StringUtils.isEmpty(url))
            {
                return false;
            }
            URL urlObj = new URL(url);
            String protocol = urlObj.getProtocol().toLowerCase();
            if (!"http".equals(protocol) && !"https".equals(protocol))
            {
                return false;
            }
            String host = urlObj.getHost();
            if (StringUtils.isEmpty(host))
            {
                return false;
            }
            java.net.InetAddress[] addresses = java.net.InetAddress.getAllByName(host);
            for (java.net.InetAddress address : addresses)
            {
                if (address.isLoopbackAddress() 
                    || address.isAnyLocalAddress() 
                    || address.isLinkLocalAddress() 
                    || address.isSiteLocalAddress()
                    || address.isMulticastAddress())
                {
                    return false;
                }
                
                // 校验自定义IPv4私有和保留地址范围
                byte[] ipBytes = address.getAddress();
                if (ipBytes.length == 4)
                {
                    int firstOctet = ipBytes[0] & 0xFF;
                    int secondOctet = ipBytes[1] & 0xFF;
                    int thirdOctet = ipBytes[2] & 0xFF;
                    
                    // CGNAT: 100.64.0.0/10
                    if (firstOctet == 100 && (secondOctet >= 64 && secondOctet <= 127))
                    {
                        return false;
                    }
                    // Benchmarking: 198.18.0.0/15
                    if (firstOctet == 198 && (secondOctet == 18 || secondOctet == 19))
                    {
                        return false;
                    }
                    // Documentation / Test-Net: 192.0.2.0/24, 198.51.100.0/24, 203.0.113.0/24
                    if (firstOctet == 192 && secondOctet == 0 && thirdOctet == 2)
                    {
                        return false;
                    }
                    if (firstOctet == 198 && secondOctet == 51 && thirdOctet == 100)
                    {
                        return false;
                    }
                    if (firstOctet == 203 && secondOctet == 0 && thirdOctet == 113)
                    {
                        return false;
                    }
                    // Reserved (Class E): 240.0.0.0/4
                    if (firstOctet >= 240)
                    {
                        return false;
                    }
                }
            }
            return true;
        }
        catch (Exception e)
        {
            return false;
        }
    }
}
