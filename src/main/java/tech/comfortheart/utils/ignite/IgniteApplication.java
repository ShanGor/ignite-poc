package tech.comfortheart.utils.ignite;

import org.apache.ignite.Ignite;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.CacheAtomicityMode;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.multicast.TcpDiscoveryMulticastIpFinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.LinkedList;
import java.util.List;

@SpringBootApplication
public class IgniteApplication {
	Logger logger = LoggerFactory.getLogger(IgniteApplication.class.getSimpleName());

	public static void main(String[] args) {

		SpringApplication.run(IgniteApplication.class, args);
	}

	@Bean
	Ignite ignite() {

		TcpDiscoverySpi spi = new TcpDiscoverySpi();

		/**
		 * -DlocalPort=37500, default port for ignite is 47500
		 */
		String localPort = System.getProperty("localPort");
		if(localPort!=null && localPort.matches("\\d+")) {
			spi.setLocalPort(Integer.parseInt(localPort));
		}
		logger.info("Starting with localPort: " + spi.getLocalPort());

		TcpDiscoveryMulticastIpFinder finder = new TcpDiscoveryMulticastIpFinder();
		List<String> addresses = new LinkedList<>();
		addresses.add("Samuels-MBP.lan");
		finder.setAddresses(addresses);

		spi.setIpFinder(finder);

		IgniteConfiguration cfg = new IgniteConfiguration();
		CacheConfiguration cacheCfg = new CacheConfiguration();
		cacheCfg.setName("default");
		cacheCfg.setAtomicityMode(CacheAtomicityMode.ATOMIC);
		cacheCfg.setCacheMode(CacheMode.PARTITIONED);
		cacheCfg.setBackups(1);
		cfg.setCacheConfiguration(cacheCfg);

		// Override default discovery SPI.
		cfg.setDiscoverySpi(spi);
		cfg.setClientMode(false);
		return Ignition.start(cfg);
	}

}
