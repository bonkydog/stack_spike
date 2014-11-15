(ns stack-spike.interface.routes)

(def routes
  ["/" {"" :home
        "ships" :ships
        ["ships/" :id] :ship}])
