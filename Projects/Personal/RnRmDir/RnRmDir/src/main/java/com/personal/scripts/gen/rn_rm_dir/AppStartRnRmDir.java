package com.personal.scripts.gen.rn_rm_dir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import java.util.stream.Stream;

final class AppStartRnRmDir {

	private AppStartRnRmDir() {
	}

	public static void main(
			final String[] args) {

		final Instant start = Instant.now();

		if (args.length < 1) {

			final String helpMessage = createHelpMessage();
			System.err.println("insufficient arguments" + System.lineSeparator() + helpMessage);
			System.exit(-1);
		}

		if ("-help".equals(args[0])) {

			final String helpMessageString = createHelpMessage();
			System.out.println(helpMessageString);
			System.exit(0);
		}

		final String folderPathString = args[0];

		boolean success = false;
		try {
			success = main(folderPathString);

		} catch (final Exception exc) {
			exc.printStackTrace();
		}

		if (!success) {
			System.exit(-2);
		}

		final Duration executionTime = Duration.between(start, Instant.now());
		System.out.println("done; execution time: " + durationToString(executionTime));
	}

	private static String createHelpMessage() {
		return "usage: rn_rm_dir DIR_PATH";
	}

	private static boolean main(
			final String folderPathString) throws Exception {

		boolean success = false;

		System.out.println();
		System.out.println("--> starting rename and remove dir");

		final Path folderPath = Paths.get(folderPathString).toAbsolutePath().normalize();
		System.out.println("folder path:");
		System.out.println(folderPath);

		if (!Files.isDirectory(folderPath)) {
			System.err.println("ERROR - folder does not exist");

		} else {
			try (Stream<Path> filePathStream = Files.walk(folderPath)) {

				filePathStream
						.filter(Files::isRegularFile)
						.forEach(AppStartRnRmDir::tryClearReadOnlyFlag);
			}

			final String renamedFolderPathString = folderPath + "_TO_BE_DELETED";
			final Path renamedFolderPath = Paths.get(renamedFolderPathString);
			Files.move(folderPath, renamedFolderPath);

			final Process process = new ProcessBuilder()
					.command("cmd", "/c", "frmdir", renamedFolderPathString)
					.inheritIO()
					.start();
			process.waitFor();

			success = true;
		}
		return success;
	}

	private static void tryClearReadOnlyFlag(
			final Path filePath) {

		boolean success = false;
		try {
			success = filePath.toFile().setWritable(true);

		} catch (final Exception exc) {
			exc.printStackTrace();
		}
		if (!success) {
			System.err.println("failed to clear readonly flag of file:" +
					System.lineSeparator() + filePath);
		}
	}

	private static String durationToString(
			final Duration duration) {

		final StringBuilder stringBuilder = new StringBuilder();
		final long allSeconds = duration.get(ChronoUnit.SECONDS);
		final long hours = allSeconds / 3600;
		if (hours > 0) {
			stringBuilder.append(hours).append("h ");
		}

		final long minutes = (allSeconds - hours * 3600) / 60;
		if (minutes > 0) {
			stringBuilder.append(minutes).append("m ");
		}

		final long nanoseconds = duration.get(ChronoUnit.NANOS);
		final double seconds = allSeconds - hours * 3600 - minutes * 60 +
				nanoseconds / 1_000_000_000.0;
		stringBuilder.append(doubleToString(seconds)).append('s');

		return stringBuilder.toString();
	}

	private static String doubleToString(
			final double d) {

		final String str;
		if (Double.isNaN(d)) {
			str = "";

		} else {
			final String format;
			format = "0.000";
			final DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.US);
			final DecimalFormat decimalFormat = new DecimalFormat(format, decimalFormatSymbols);
			str = decimalFormat.format(d);
		}
		return str;
	}
}
