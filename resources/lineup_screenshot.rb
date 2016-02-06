require 'lineup'

lineup = Lineup::Screenshot.new(ARGV[0])
lineup.resolutions(ARGV[1])
lineup.urls(ARGV[2])
lineup.filepath_for_images(ARGV[3])
lineup.use_phantomjs(ARGV[4] == "true")
lineup.wait_for_asynchron_pages(ARGV[5].to_i)

if ARGV[6] == "true"
cookie =  {"name" => ARGV[7],
           "value" => ARGV[8],
           "domain" => ARGV[9],
           "path" => ARGV[10],
           "secure" => ARGV[11] == "true"}
lineup.cookie_for_experiment(cookie)
end

lineup.record_screenshot('before')