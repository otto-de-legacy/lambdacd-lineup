require 'lineup'

lineup = Lineup::Screenshot.new(ARGV[0])
lineup.resolutions(ARGV[1])
lineup.urls(ARGV[2])
lineup.filepath_for_images(ARGV[3])
lineup.difference_path(ARGV[3])
lineup.use_phantomjs(ARGV[4] == "true")
lineup.wait_for_asynchron_pages(ARGV[5].to_i)

lineup.record_screenshot('after')

lineup.compare('before', 'after')

lineup.save_json(ARGV[3])

