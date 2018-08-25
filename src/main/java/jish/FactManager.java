package jish;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Random;

public class FactManager {

	private File factFile = new File(Util.botPath + "/facts.txt");
	private List<String> facts;

	{
		try {
			facts = FileUtils.readLines(factFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Random random = new Random();
	public int size = random.nextInt(facts.size());

	@SuppressWarnings("deprecation")
	public String randomFacts() {
		facts.remove(size);
		return facts.get(size);
	}

	public Integer arraySize() {
		return facts.size();
	}

	public void removeBlankLines() {
		try {
			List<String> lines = FileUtils.readLines(factFile);

			lines.removeIf(line -> line.trim().isEmpty());
			FileUtils.writeLines(new File(String.valueOf(factFile)), lines);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
}