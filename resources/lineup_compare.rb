require 'lineup'

lineup = Lineup::Screenshot.new(ARGV[0])
lineup.resolutions(ARGV[1])
lineup.urls(ARGV[2])
lineup.filepath_for_images(ARGV[3])
lineup.difference_path(ARGV[3])
lineup.use_phantomjs(ARGV[4] == "true")
lineup.wait_for_asynchron_pages(ARGV[5].to_i)

cookies = JSON.parse(ARGV[6])
cookies_with_symbol_keys = []
cookies.each do |cookie|
    cookies_with_symbol_keys.push(cookie.inject({}) { |element, (symbol, value)| element[symbol.to_sym] = value; element })
end
lineup.cookies(cookies_with_symbol_keys)

localStorage = JSON.parse(ARGV[7])
lineup.localStorage(localStorage)

lineup.record_screenshot('after')

begin
    lineup.compare('before', 'after')
    lineup.save_json(ARGV[3])
rescue RuntimeError => e
    puts ARGV[0]
    puts e
end


