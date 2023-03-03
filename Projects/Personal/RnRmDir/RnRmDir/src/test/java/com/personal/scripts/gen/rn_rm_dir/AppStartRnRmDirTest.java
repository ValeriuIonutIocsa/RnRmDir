package com.personal.scripts.gen.rn_rm_dir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class AppStartRnRmDirTest {

	@Test
	void testMain() {

		final String dirPathString;
		final int input = Integer.parseInt("2");
		if (input == 1) {
			dirPathString = "D:\\casdev\\td5\\da\\mdd";
		} else if (input == 2) {
			dirPathString = "D:\\casdev\\td5\\vw\\h02\\700\\archive_0u0_6\\bld";
		} else {
			throw new RuntimeException();
		}

		final String[] args = { dirPathString };
		AppStartRnRmDir.main(args);

		final Path dirPath = Paths.get(dirPathString);
		Assertions.assertFalse(Files.isDirectory(dirPath));
	}

	@Test
	void testHelp() {

		AppStartRnRmDir.main(new String[] { "-help" });
	}

	@Test
	void testNoArgs() {

		AppStartRnRmDir.main(new String[] {});
	}
}
