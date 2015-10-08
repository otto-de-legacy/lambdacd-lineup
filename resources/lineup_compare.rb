require 'lineup'

lineup = Lineup::Screenshot.new(ARGV[0] + '://' + ARGV[1] + '.' + ARGV[2])
lineup.resolutions(ARGV[3])
lineup.urls(ARGV[4])
lineup.filepath_for_images(ARGV[5])
lineup.difference_path(ARGV[5])

lineup.record_screenshot('after')

lineup.compare('before', 'after')

lineup.save_json(ARGV[5])