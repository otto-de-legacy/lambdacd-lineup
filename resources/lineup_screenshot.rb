require 'lineup'

lineup = Lineup::Screenshot.new(ARGV[0] + '://' + ARGV[1] + '.' + ARGV[2])
lineup.resolutions(ARGV[3])
lineup.urls(ARGV[4])
lineup.filepath_for_images(ARGV[5])
lineup.use_phantomjs(ARGV[6] == "true")

lineup.record_screenshot('before')