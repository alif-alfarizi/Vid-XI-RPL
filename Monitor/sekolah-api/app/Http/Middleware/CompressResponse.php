<?php

namespace App\Http\Middleware;

use Closure;
use Illuminate\Http\Request;

class CompressResponse
{
    /**
     * Handle an incoming request.
     *
     * @param  \Illuminate\Http\Request  $request
     * @param  \Closure  $next
     * @return mixed
     */
    public function handle(Request $request, Closure $next)
    {
        $response = $next($request);

        // Compress response untuk mengurangi bandwidth
        if (
            function_exists('gzencode') &&
            !$response->headers->has('Content-Encoding') &&
            $request->header('Accept-Encoding') &&
            str_contains($request->header('Accept-Encoding'), 'gzip')
        ) {

            $content = $response->getContent();
            if (strlen($content) > 1024) { // Only compress if > 1KB
                $compressed = gzencode($content, 6);
                $response->setContent($compressed);
                $response->headers->set('Content-Encoding', 'gzip');
                $response->headers->set('Content-Length', strlen($compressed));
            }
        }

        return $response;
    }
}
